package timer;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import jdk.nashorn.tools.Shell;

public class Task {

    private static File movedFile;

    Timer timer;
    private final Toolkit toolkit;
    private String configurationFile;

    public Task(int seconds) {
        toolkit = Toolkit.getDefaultToolkit();
        timer = new Timer();
        timer.schedule(new NewTask(), 0, 1 * 1000);
    }

    class NewTask extends TimerTask {

        Timer counter = null;

        public void run() {
            if (counter == null) {
                System.out.println("Reading");
                File source = new File("coloca o diretorio que vai ser colocado os arquivos para converter");
                File destiny = new File("coloca o diretorio que vai aparecer os relatorios convertidos");

                try {
                    copyAll(source, destiny, false);
                } catch (IOException ex) {
                }

            } else {
                System.out.println("Acabou de executar");
                System.exit(0);
            }
        }
    }

    public static void copyAll(File source, File destiny, boolean modification) throws IOException, UnsupportedOperationException {

        if (!destiny.exists()) {
            destiny.mkdir();
        }
        if (!source.isDirectory()) {
            throw new UnsupportedOperationException("Origem deve ser um diretório");
        }
        if (!destiny.isDirectory()) {
            throw new UnsupportedOperationException("Destino deve ser um diretório");
        }
        File[] files = source.listFiles();

        for (int i = 0; i < files.length; ++i) {

            System.out.println("Copiando arquivo: " + files[i].getName());
            movedFile = new File(destiny + "\\" + files[i].getName());
            copy(files[i], movedFile, modification);
            files[i].delete();
            runRep2Excel(movedFile);
        }
    }

    public static void copy(File source, File destiny, boolean modification) throws IOException {

        if (destiny.exists() && !modification) {
            return;
        }
        source.renameTo(destiny);

    }

    public static String runRep2Excel(File movedFile) throws IOException {
        boolean success = false;
        String result;

        Process p;
        BufferedReader input;
        StringBuffer cmdOut = new StringBuffer();
        String lineOut = null;
        int numberOfOutline = 0;

        try {

            p = Runtime.getRuntime().exec("Diretorio que esta o repToExcel\\Rep2Excel\\rep2excel-trial\\Rep2excel.exe  -i:" + movedFile.getPath());

            input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((lineOut = input.readLine()) != null) {
                if (numberOfOutline > 0) {
                    cmdOut.append("\n");
                }
                cmdOut.append(lineOut);
                numberOfOutline++;
            }

            result = cmdOut.toString();
            success = true;
            input.close();

        } catch (IOException e) {
            result = String.format("Falha ao executar comando %s. Erro: %s", Shell.COMMANDLINE_ERROR, e.toString());
        }

        if (!success) {
            throw new IOException(result);
        }

        return result;
    }
}
