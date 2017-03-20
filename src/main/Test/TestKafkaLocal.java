import info.batey.kafka.unit.KafkaUnitRule;
import it.daplab.finance.yahoo.Stock;
import it.daplab.finance.yahoo.YahooFinance;
import it.daplab.utils.CompaniesLister;
import kafka.producer.KeyedMessage;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by christophebovigny on 04.03.17.
 */
public class TestKafkaLocal {

    private final static boolean ALSO_HISORICAL = true;
    private final static String STOCK_FILE = CompaniesLister.US_TOP20;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


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

    @Rule
    public KafkaUnitRule kafkaUnitRule = new KafkaUnitRule(5002, 5003);

    /*
    @Test
    public void junitRuleShouldHaveStartedKafka() throws Exception {
        String testTopic = "TestTopic";
        kafkaUnitRule.getKafkaUnit().createTopic(testTopic);

        //Producer<String, String> producer = new KafkaProducer<>(props);
        // producer.send(new ProducerRecord<String, String>("my-topic", "key", "value"));

        //ProducerRecord<String, String> keyedMessage = new ProducerRecord<String, String>(testTopic,"key","value");
        KeyedMessage<String, String> keyedMessage = new KeyedMessage<>(testTopic, "key", "value");

        kafkaUnitRule.getKafkaUnit().sendMessages(keyedMessage);
        List<String> messages = kafkaUnitRule.getKafkaUnit().readMessages(testTopic, 1);

        assertEquals(Arrays.asList("value"), messages);
    }
    */

    @Test
    public void junitStock() throws Exception {

        Integer intervalMS = 1000;
        CompaniesLister cl = new CompaniesLister();
        List<String> stocks = cl.listCompanies(STOCK_FILE);

        String testTopic = "TestTopic2";
        kafkaUnitRule.getKafkaUnit().createTopic(testTopic);
        int i=0;
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

                message = symbol2stock.get(symbol).toString();
                Stock s = symbol2stock.get(symbol);

                StringBuilder messagefinal = new StringBuilder();
                String delimiter = "|";

                messagefinal.append(s.getSymbol());
                messagefinal.append(delimiter);
                messagefinal.append(s.getName());
                messagefinal.append(delimiter);
                messagefinal.append(s.getQuote().getAsk().doubleValue());
                messagefinal.append(delimiter);
                messagefinal.append(s.getQuote().getBid().doubleValue());
                messagefinal.append(delimiter);
                messagefinal.append(s.getQuote().getPrice().doubleValue());
                messagefinal.append(delimiter);
                messagefinal.append(s.getQuote().getVolume());

                messagefinal.toString();



                //System.out.println(message);
                // message = writeAsString(symbol2stock.get(symbol));
                //message = writeAsJson(symbol2stock.get(symbol));




                keyedMessage = new KeyedMessage<String, String>(testTopic, requestDate, messagefinal.toString());
                printKeyedMessage(keyedMessage);
                kafkaUnitRule.getKafkaUnit().sendMessages(keyedMessage);

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

        i =i++;
            if(i==3){
                kafkaUnitRule.getKafkaUnit().shutdown();
                break;
            }
        }
    }

}
