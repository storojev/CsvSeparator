/**
 *   class CSVSeparator.java (Version 1.0.0.0)
 *
 *   CSVSeparator class reads .CSV files from the "input" directory,
 *   Collects the latest version of the customer records,
 *   Separates customer records by the provider name,
 *   Outputs .CSV files separated by the provider name in the "output" directory.
 *
 *   Note: contains nested class SortByLastNameFirstName.
 */

package csvseparator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

///////////////////////////////////////////////////////////////////////////////
public class CSVSeparator {

    private final String INPUT_DIR = "input";
    private final String OUTPUT_DIR = "output";
    private final String FILE_EXT = ".csv";
    private final String DELIMITER = ",";
    private String headers = "";

    //---------------------------------------------------------------------------
    public static void main(String[] args) {

        CSVSeparator csvSeparator = new CSVSeparator();

        // map key is unique customer id - value is customer object
        Map < String, Customer > customerList = new HashMap < String, Customer > ();

        try {
            // read customer records from CSV input file
            customerList = csvSeparator.ReadCSVFiles();
        } catch ( IOException ioe ) {
            System.out.println("A problem occurred reading *.csv files!");
        } catch ( NumberFormatException nfe ) {
            System.out.println("A problem occurred parsing *.csv files!");
        }

        // sort customer records by: last name, first name
        List < Customer > sortedCustomerList = new ArrayList < > (customerList.values());
        Collections.sort(sortedCustomerList, new SortByLastNameFirstName());

        try {
            // write out customer records files separated by provider name
            csvSeparator.WriteCSVFiles(sortedCustomerList);

        } catch ( IOException ioe ) {
            System.out.println("A problem occurred writing *.csv files!");
        }
    }

    //---------------------------------------------------------------------------
    private Map < String, Customer > ReadCSVFiles() throws IOException, NumberFormatException {

        // map key is unique customer id - value is customer object
        Map < String, Customer > customerList = new HashMap < String, Customer > ();

        // check if input directory exists
        File dir = new File(INPUT_DIR);
        if ( !dir.exists() ) {
            System.out.println(INPUT_DIR + " directory does not exists!");
        } else {
            File[] files = new File(INPUT_DIR).listFiles();
            for ( File file: files ) {
                if ( file.isFile() ) {
                    String csvFileName = file.getName();

                    // make sure it is .CSV file
                    if ( csvFileName.toLowerCase().endsWith(FILE_EXT) ) {
                        String csvFilePath = INPUT_DIR + "/" + csvFileName;
                        try ( BufferedReader br = new BufferedReader(new FileReader(csvFilePath)) ) {
                            String row = "";
                            int rowNum = 0;
                            while ( (row = br.readLine()) != null ) {
                                rowNum++;
                                // store header row for the output(s)
                                if ( rowNum == 1 ) {
                                    this.headers = row;
                                    continue;
                                }

                                String[] fields = row.split(DELIMITER);

                                // check if the CSV structure is valid
                                if ( fields.length == 5 ) {
                                    Customer customer = new Customer();
                                    customer.setId(fields[0]);                          // col: Id
                                    customer.setFirst(fields[1]);                       // col: First Name
                                    customer.setLast(fields[2]);                        // col: Las Name
                                    customer.setVersion(Integer.parseInt(fields[3]));   // col: Version
                                    customer.setProvider(fields[4]);                    // col: Provider

                                    // check if the customer record is already in the list
                                    if ( customerList.containsKey(customer.getId()) ) {
                                        // check if the customer entry has older version of the record
                                        if ( customerList.get(customer.getId()).getVersion() < customer.getVersion() ) {
                                            customerList.replace(customer.getId(), customer);
                                        }
                                    } else {
                                        // add new customer record to the list
                                        customerList.put(customer.getId(), customer);
                                    }
                                } else {
                                    System.out.println("Invalid CSV structure. Row number: " + rowNum + ", Data: " + row);
                                }
                            }
                        }

                        System.out.println("Processed " + csvFileName);
                    }
                }
            }
        }

        return customerList;
    }

    //---------------------------------------------------------------------------
    private void WriteCSVFiles(List < Customer > sortedCustomerList) throws IOException {

        // separate customer list into separate list(s) by provider name
        // map key is provider name - value is a list of customer object(s) already sorted
        Map < String, List < Customer >> providerMap = new HashMap < String, List < Customer >> ();

        for ( Customer customer: sortedCustomerList ) {
            // check if the provider name is already in the map
            if ( providerMap.containsKey(customer.getProvider()) ) {
                providerMap.get(customer.getProvider()).add(customer);
            } else {
                // add new provider entry and add customer object to a list
                providerMap.put(customer.getProvider(), new ArrayList < Customer > ());
                providerMap.get(customer.getProvider()).add(customer);
            }
        }

        // check if output directory exists, if not make one
        File dir = new File(OUTPUT_DIR);
        if ( !dir.exists() ) {
            dir.mkdir();
        }

        // write out output file(s) separated by provider, name output file with a key = provider name
        for ( String provider: providerMap.keySet() ) {
            // TODO: check provider name string for invalid filename characters ...
            File file = new File(OUTPUT_DIR + "/" + provider + FILE_EXT);

            // reuse col headers from the input file(s)
            String rows = headers;

            // check if file exists
            if ( !file.exists() ) {
                file.createNewFile();
            }

            for ( Customer customer: providerMap.get(provider) ) {
                rows += "\n";
                rows += customer.getId() + DELIMITER;
                rows += customer.getFirst() + DELIMITER;
                rows += customer.getLast() + DELIMITER;
                rows += String.valueOf(customer.getVersion()) + DELIMITER;
                rows += customer.getProvider();
            }

            try ( BufferedWriter bw = new BufferedWriter(new FileWriter(file)) ) {
                bw.write(rows);
            }

            System.out.println("Created output: " + file.getName());
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    static class SortByLastNameFirstName implements Comparator < Customer > {

        @Override
        public int compare(Customer customer1, Customer customer2) {
            /*
                sort by:
                  1) last name
                  2) first name
            */

            int compareResult = customer1.getLast().compareTo(customer2.getLast());
            if ( compareResult == 0 ) {
                compareResult = customer1.getFirst().compareTo(customer2.getFirst());
            }
            return compareResult;
        }
    }

    //---------------------------------------------------------------------------

}
