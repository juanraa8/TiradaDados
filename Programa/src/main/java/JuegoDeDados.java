import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

class TiradorDeDados implements Callable<Integer> {
    private static final Logger LOGGER = Logger.getLogger(TiradorDeDados.class.getName());
    private int numCaras;

    public TiradorDeDados(int numCaras) {
        this.numCaras = numCaras;
    }

    @Override
    public Integer call() throws Exception {
        Random rand = new Random();
        int resultado = rand.nextInt(1, numCaras + 1);
        LOGGER.fine("Resultado del dado: " + resultado);
        return resultado;
    }
}

public class JuegoDeDados {
    public static void main(String[] args) {
        int numDados = 6;
        int[] resultados = tirarDados(numDados);

        // Mostrar resultados en la consola
        for (int i = 0; i < numDados; i++) {
            System.out.println("Resultado del dado " + (i + 1) + ": " + resultados[i]);
        }

        // Mostrar estadísticas en el log
        double media_jugadas = calcularMedia(resultados);
        double desviacion_estandar_jugadas = calcularDesviacionEstandar(resultados, media_jugadas);
        int valor_mas_frecuente_jugadas = calcularModa(resultados);

        Logger logger = Logger.getLogger(JuegoDeDados.class.getName());
        FileHandler fh;
        try {
            fh = new FileHandler("JuegoDeDados.log", true);
            logger.addHandler(fh);
            logger.log(Level.INFO, "Media de las {0} jugadas: {1}", new Object[] {numDados, media_jugadas});
            logger.log(Level.INFO, "Desviación estándar de las {0} jugadas: {1}", new Object[] {numDados, desviacion_estandar_jugadas});
            logger.log(Level.INFO, "Valor más frecuente de las {0} jugadas: {1}", new Object[] {numDados, valor_mas_frecuente_jugadas});
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] tirarDados(int numDados) {
        TiradorDeDados[] tiradores = new TiradorDeDados[numDados];
        ExecutorService executor = Executors.newFixedThreadPool(numDados);
        Future<Integer>[] futuros = new Future[numDados];
        int[] resultados = new int[numDados];

        for (int i = 0; i < numDados; i++) {
            tiradores[i] = new TiradorDeDados(6); // crear un dado con 6 caras
            futuros[i] = executor.submit(tiradores[i]); // enviar la tarea al ejecutor
        }

        for (int i = 0; i < numDados; i++) {
            try {
                Integer resultado = futuros[i].get(); // obtener el resultado de la tarea
                resultados[i] = resultado.intValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        return resultados;
    }

    public static double calcularMedia(int[] resultados) {
        int sum = 0;
        for (int i = 0; i < resultados.length; i++) {
            sum += resultados[i];
        }
        return (double) sum / resultados.length;
    }

    public static double calcularDesviacionEstandar(int[] resultados, double media) {
        double varianceSum = 0;
        for (int i = 0; i < resultados.length; i++) {
            varianceSum += Math.pow(resultados[i] - media, 2);
        }
        return Math.sqrt(varianceSum / resultados.length);
    }

    public static int calcularModa(int[] resultados) {
        int maxCount = 0;
        int moda = -1;
        int[] counts = new int[resultados.length];

        for (int i = 0; i < resultados.length; i++) {
            counts[i] = 0;
            for (int j = 0; j < resultados.length; j++) {
                if (resultados[j] == resultados[i]) {
                    counts[i]++;
                }
            }
            if (counts[i] > maxCount) {
                maxCount = counts[i];
                moda = resultados[i];
            }
        }

        return moda;
    }
}