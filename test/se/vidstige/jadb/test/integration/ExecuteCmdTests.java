package se.vidstige.jadb.test.integration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import se.vidstige.jadb.JadbConnection;

import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.managers.PackageManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class ExecuteCmdTests {
    private static JadbConnection jadb;
    private static JadbDevice jadbDevice;

    @Parameterized.Parameter
    public String input;


    @Parameterized.Parameters(name="Test {index} input={0}")
    public static Collection input() {
        List<Object> list = new ArrayList<>();
        list.addAll(Arrays.asList(new Object[]{"asd", "asf dsa", "sdf&g", "sd& fg", "da~f", "asd'as", "a¡f", "asüd", "adös tz"}));
        for (int i = 32; i <= 126; i++) {
            list.add(Character.toString((char)i));
        }
        return list;
    }

    @BeforeClass
    public static void connect() {
        try {
            jadb = new JadbConnection();
            jadb.getHostVersion();
            jadbDevice = jadb.getAnyDevice();
        } catch (Exception e) {
            org.junit.Assume.assumeNoException(e);
        }
    }



    @Test
    public void testExecuteWithSpecialChars() throws Exception {

        if (containsNotAllowedUTFCode(input)) {
            Assert.assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
                @Override
                public void run() throws Throwable {
                    jadbDevice.execute("echo", input);
                }
            });
        } else {
            InputStream response = jadbDevice.execute("echo", input);
            ResponseReader responseReader = new ResponseReader(response);
            responseReader.start();
            responseReader.join(1000L);
            String ret = responseReader.output;
            //remove newline
            ret = ret.replaceAll("\n$", "");
            Assert.assertEquals(input, ret);
        }
    }


    private boolean containsNotAllowedUTFCode(String s) {
        for (byte b : s.getBytes(StandardCharsets.UTF_8)) {
            if (b < 32 || b > 126) {
                return true;
            }
        }
        return false;
    }

    private class ResponseReader extends Thread {
        public String output;
        private InputStream response;

        ResponseReader(InputStream response) {
            super();
            this.response = response;
        }

        @Override
        public void run() {
            try {
                StringBuilder textBuilder = new StringBuilder();
                try (Reader reader = new BufferedReader(new InputStreamReader
                        (response, Charset.forName(StandardCharsets.UTF_8.name())))) {
                    int c = 0;
                    while ((c = reader.read()) != -1) {
                        textBuilder.append((char) c);
                    }
                }
                output = textBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
