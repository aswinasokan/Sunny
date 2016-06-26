package com.latestarter.sunny.weather;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Connect to URL to fetch data
 */
public class UrlDataFetcher {

    public String fetchStringResponse(URL url) throws IOException {

        HttpURLConnection connection = null;

        try {
            // Create the request and open the connection
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Read the input stream into a String
            try (InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(inputStream)) {

                return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
