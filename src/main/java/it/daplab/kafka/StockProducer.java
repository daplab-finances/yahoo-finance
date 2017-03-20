package it.daplab.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.daplab.finance.yahoo.Stock;
import it.daplab.finance.yahoo.YahooFinance;
import it.daplab.utils.CompaniesLister;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

public class StockProducer {
	private final static boolean ALSO_HISORICAL = true;
	private final static String STOCK_FILE = CompaniesLister.US_TOP20;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public static void main(String[] args) {
		StockProducer sp = new StockProducer();
		sp.writeStocks(args);
	}

	private void writeStocks(String[] args) {
		if (args.length != 3) {
			System.out.println(
					"Usage: java -cp finance-kafka-from-yahoo-0.0.1-SNAPSHOT-jar-with-dependencies.jar it.himyd.kafka.StockProducer <kafka-broker> <topics_seperated_by_comma> <request_delay_ms>");
			System.exit(1);
		}

		Integer intervalMS = Integer.parseInt(args[2]);
		String[] topics = args[1].split(",");

		Properties props = new Properties();
		props.put("metadata.broker.list", args[0].toString());
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(props);
		Producer<String, String> producer = new Producer<String, String>(config);

		CompaniesLister cl = new CompaniesLister();
		List<String> stocks = cl.listCompanies(STOCK_FILE);


		while (true) {
			Map<String, Stock> symbol2stock;
			KeyedMessage<String, String> keyedMessage;
			String requestDate;
			String message;

			symbol2stock = getStocks(stocks);

			if (symbol2stock == null) {
				continue;
			}

			System.out.println("sending start: " + sdf.format(new Date()));

			for (String symbol : symbol2stock.keySet()) {
				// System.out.println("message start: " + sdf.format(new Date()));

				requestDate = new Date().getTime() + "";
                //System.out.println(requestDate);
               // System.out.println(topics[0]);
				message = writeAsString(symbol2stock.get(symbol));
                //message = writeAsJson(symbol2stock.get(symbol));



				keyedMessage = new KeyedMessage<String, String>(topics[0], requestDate, message);
				producer.send(keyedMessage);

				// printKeyedMessage(keyedMessage);

				// System.out.println("message end: " + sdf.format(new Date()));
			}

			System.out.println("sending end: " + sdf.format(new Date()));
			System.out.println("");

			try {
				Thread.sleep(intervalMS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		// producer.close();
	}

	private String writeAsString(Stock stock) {
		StringBuilder message = new StringBuilder();
		String delimiter = "|";

		message.append(stock.getSymbol());
		message.append(delimiter);


		message.append(stock.getQuote().getPrice().doubleValue());
		message.append(delimiter);


		message.append(stock.getQuote().getVolume());

		message.append(delimiter);

		if (stock.getQuote().getLastTradeTime() != null) {
			message.append(stock.getQuote().getLastTradeTime().getTimeInMillis());
		} else {
			message.append("null");
		}

		return message.toString();
	}

	private String writeAsJson(Stock s) {
		String message;

		try {
			// System.out.println("json start: " + sdf.format(new Date()));
			message = s.toJSONString();
			// System.out.println("json end: " + sdf.format(new Date()));

		} catch (JsonProcessingException e) {
			message = null;
			System.err.println("error parsing: " + s.getSymbol());
			e.printStackTrace();
		}
		return message;

	}

	private static void printKeyedMessage(KeyedMessage<String, String> message) {
		String messageString = message.message();
		// System.out.println(message.key());

		// Date requestDate = new Date(Long.parseLong(message.key()));
		// String millisDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(requestDate);
		// System.out.println(millisDate);

		if (messageString.length() > 40) {
			System.out.println(messageString.substring(0, 40));
		} else {
			System.out.println(messageString);
		}
	}

	public static Map<String, Stock> getStocks(List<String> stocksString) {
		Map<String, Stock> stocks = null;

		try {
			YahooFinance.logger.setLevel(Level.WARNING);
			stocks = YahooFinance.get(stocksString.toArray(new String[stocksString.size()]), ALSO_HISORICAL);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stocks;
	}

	public static Stock getStock(String stockString) {
		Stock stock = null;

		try {
			YahooFinance.logger.setLevel(Level.WARNING);
			stock = YahooFinance.get(stockString);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stock;
	}

	public static void validateStock(String[] symbols) {
		for (String symbol : symbols) {
			System.out.println("checking: " + symbol);
			getStock(symbol);
		}

	}

	private void cleanArray(List<String> stocks) {
		System.out.println("Cleaning array of companies...");

		for (String s : stocks) {
			try {
				Stock stock = YahooFinance.get(s);
				System.out.println("ok: " + stock.getSymbol());
			} catch (Exception e) {
				boolean removed = stocks.remove(s);
				if (removed) {
					System.out.println("removed: " + s);
				}
				// e.printStackTrace();
			}
		}

	}

}
