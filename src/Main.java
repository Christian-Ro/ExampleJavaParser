import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.println("File Source: ");
        String file = sc.next();
        List<String> list = new ArrayList<>();

        //Read the file
        try(Stream<String> stream = Files.lines(Paths.get(file))){

            //Store the contents of the file into a list
            list = stream
                    .collect(Collectors.toList());
        }
        catch (IOException e){
            e.printStackTrace();
        }

        customer(list);   //For XML output
        orders(list); //For JSON output
        product(list);  //For CSV output

        System.out.println("Everything created, please check folder for files.");
    }


    //Method to get only the customer information
    private static void customer(List<String> list){

        List<String> customer;

        customer = list.stream()
                .filter(customers -> customers.startsWith("\"customer\""))
                .collect(Collectors.toList());

        //System.out.println(customer); //Testing purposes only
        toXML(customer);

    }

    //Method to get order and order-line information
    private static void orders(List<String> list){

        List<String> orders;

        orders = list.stream()
                .filter(order -> order.startsWith("\"order\"") || order.startsWith("\"order-line\""))
                .collect(Collectors.toList());

        //System.out.println(orders);   //Testing purposes only
        toJSON(orders);
    }


    //Method to get product information
    private static void product(List<String> list){

        List<String> products;

        products = list.stream().skip(1)
                .filter(product -> product.startsWith("\"product\""))   //Search only for products to include in the csv file
                .map(product -> product.replace("\"product\",", ""))    //Remove "product" word from the list
                .collect(Collectors.toList());

        toCSV(products);    //Pass the products to the method toCSV for output into a cvs file
    }

    //Convert the product information to a csv file
    private static void toCSV(List<String> list){

        List<List<String>> allCsvWords = new ArrayList<>();
        String[] words;

        for (String lists : list) {
            words = lists.split(",");
            List<String> csvWords = new ArrayList<>(Arrays.asList(words));
            if (csvWords.size() == 4) {
                csvWords.add("USD");
            }

//            if (Character.isDigit(csvWords.get(2).charAt(0))) {
//                csvWords.add(2," ");
//            }

            allCsvWords.add(csvWords);
        }

        //Try and create a file named "products.csv
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("E:\\Users\\Christian\\output_files\\products.csv"))){

            writer.write("sku, name, brand, price, currency");
            writer.newLine();

            //Iterate the list to write on the file
            for (int i = 0; i < allCsvWords.size(); i++) {
                for (int j = 0; j < allCsvWords.get(i).size(); j++) {
                    writer.write(allCsvWords.get(i).get(j).replace("\"", "") + ",");
                    //System.out.println(allCsvWords.get(i).get(j));  //Testing purposes only
                }
                writer.newLine();
                //System.out.println("\n");   //Testing purposes only
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    //Method to convert Customers into a XML format
    private static void toXML(List<String> list){

        List<List<String>> customers = new ArrayList<>();
        //Split the customer into their id, name, email, age, and gender. Store each customer into a list.
        for(String lists : list){
            String[] cus = lists.split(",");
            List<String> temp = Arrays.asList(cus);
            customers.add(temp);
        }

        String header = "<?xml version=\"1.0\"?> \n <customer> \n";
        StringBuilder toXML = new StringBuilder(header);
        String xml;

        //Parse into xml format
        //Should replace this later with Java 8 stream
        for(int i = 0; i < customers.size(); i++){
            if(customers.get(i).size() == 6){
                xml = "\t <" + customers.get(i).get(0).replace("\"", "") + "> \n \t \t"
                + "<id>" + customers.get(i).get(1).replace("\"", "") + "</id> \n \t \t"
                + "<name>" + customers.get(i).get(2).replace("\"", "") + "</name> \n \t \t"
                + "<email>" + customers.get(i).get(3).replace("\"", "") + "</email> \n \t \t"
                + "<age>" + customers.get(i).get(4) + "</age>\n \t \t"
                + "<gender>" + customers.get(i).get(5).replace("1", "male").replace("2", "female") + "</gender> \n"
                + "\t </customer> \n";
                toXML.append(xml);
            }
            else if(customers.get(i).size() == 5){
                xml = "\t <" + customers.get(i).get(0).replace("\"", "") +"> \n \t \t"
                + "<id>" + customers.get(i).get(1).replace("\"", "") + "</id> \n \t \t"
                + "<name>" + customers.get(i).get(2).replace("\"", "") + "</name> \n \t \t"
                + "<email>" + customers.get(i).get(3).replace("\"", "") + "</email>\n \t \t"
                + "<gender>" + customers.get(i).get(4).replace("1", "male").replace("2", "female") + "</gender> \n"
                + "\t </customer> \n";
                toXML.append(xml);
            }
            else
                System.out.println("Can't write file. Unknown number of id's"); //Warning in case there is more identifiers for customers
        }
        toXML.append("</customers>");
        //System.out.println(toXML);    //Testing the output of the xml

        //Writing to xml file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("E:\\Users\\Christian\\output_files\\customers.xml"))){
            writer.write(toXML.toString());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    //Method to convert orders and order lines to JSON format
    private static void toJSON(List<String> list){

        List<String> orders = new ArrayList<>();
        StringBuilder json = new StringBuilder("{ \n \t \"orders\": [");
        String toJson;
        int k;
        int index = 0;
        int orderIds = 0;
        int orderIdCheck = 0;

        //Get everything as separate words into an arraylist
        for(int i = 0; i < list.size(); i++){
            String[] parts = list.get(i).split(",");
            for(int j = 0; j < parts.length; j++){
                orders.add(parts[j]);
            }
        }
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).equals("\"order\"")) {
                orderIds++;
            }
        }
        //System.out.println(orders);   //Testing purposes only
        for(k = 0; k < orders.size(); k++){
            if (orders.get(k).equals("\"order\"")) {
                index = Integer.parseInt(orders.get(k+2));
                double temp = Double.parseDouble(orders.get(k+3)) + Double.parseDouble(orders.get(k+4));
                toJson = "\n \t { \n  \t \"id\":" + orders.get(k+1) +  ", \n \t \"head\" : {" + "\n \t \t \"sub_total\":" + orders.get(k+3) + ", \n \t" +"\"tax\":" + orders.get(k+4) + ", \n \t" + "\"total\":" + temp + ", \n \t" + "\"customer\":" + orders.get(k+6) + "\n \t },\n \t \t \"lines\": [{ \n \t \t \t";
                json.append(toJson);
                k += 6;
                orderIdCheck++;
            } else if (orders.get(k).equals("\"order-line\"")) {
                double temp = Double.parseDouble(orders.get(k+3)) * Double.parseDouble(orders.get(k+4));
                toJson = "\"position\":" + orders.get(k + 1) + ",\n \t \t \t \"name\":" + orders.get(k+2) + ",\n \t \t \t \"price\":" + orders.get(k+3) + ",\n \t \t \t \"quantity\":" + orders.get(k+4) + ",\n \t \t \t \"row_total\":" + temp;
                json.append(toJson);
                if (index == 2) {
                    toJson = "\n \t \t \t}, \n \t \t \t { \n \t \t \t";
                    json.append(toJson);
                    index--;
                } else if (index == 1 && (orderIdCheck == orderIds)) {
                    toJson = "\n \t \t \t} \n \t \t ] \n \t }";
                    json.append(toJson);
                } else if (index == 1 && (orderIdCheck != orderIds)){
                    toJson = "\n \t \t \t} \n \t \t ] \n \t },";
                    json.append(toJson);
                }
            }
        }
        toJson = "\n \t ] \n }";
        json.append(toJson);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("E:\\Users\\Christian\\output_files\\orders.json"))){
            writer.write(json.toString());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
