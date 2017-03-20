package it.daplab.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CompaniesLister {
	public static final String EXAMPLE = "/company-lists/stocks-example.csv";
	public static final String EU_EXAMPLE = "/company-lists/stocks-eu-example.csv";
	public static final String US_EXAMPLE = "/company-lists/stocks-us-example.csv";

	public static final String US_TOP20 = "/company-lists/stocks-us-top20.csv";

	public static final String FTSE100 = "/company-lists/eu/ftse100.csv";

	public static final String LONDON_SE = "/company-lists/eu/L.csv";
	public static final String FRAKFURT_SE = "/company-lists/eu/DE.csv";
	public static final String MILAN_SE = "/company-lists/eu/M.csv";

	public static final String AMEX_COMPANY_LIST = "/company-lists/usa/amex-company-list.csv";
	public static final String NASDAQ_COMPANY_LIST = "/company-lists/usa/nasdaq-company-list.csv";
	public static final String NYSE_COMPANY_LIST = "/company-lists/usa/nyse-company-list.csv";

	public List<String> listCompanies(String csvFile) {
		List<String> companies = new ArrayList<String>();

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {
			InputStream is = getClass().getResourceAsStream(csvFile);
			InputStreamReader isr = new InputStreamReader(is);
			br = new BufferedReader(new BufferedReader(isr));
			// br.readLine(); // skip description

			while ((line = br.readLine()) != null) {
				String[] company = line.split(cvsSplitBy);
				String companyName = company[0];
				companyName = companyName.trim();

				companies.add(companyName);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return companies;

	}

	public String[] listCompaniesArray(String csvFile) {
		List<String> companiesList = listCompanies(csvFile);
		String[] companies = companiesList.toArray(new String[companiesList.size()]);
		return companies;
	}

	public static void main(String[] args) {
		CompaniesLister cl = new CompaniesLister();
		List<String> companies = cl.listCompanies(CompaniesLister.LONDON_SE);
		System.out.println("companies # " + companies.size());
		System.out.println(companies.toString());

		for (String company : companies) {
			System.out.println(company);
		}
	}

}
