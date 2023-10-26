package com.project;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

public class Controller0 {
    Random random = new Random();

    @FXML
    private Button button0, boton1, boton2, boton3;
    @FXML
    private AnchorPane container;
    @FXML
    private Label percentatge0, percentatge1, percentatge2;
    @FXML
    private ProgressBar progress0, progress1, progress2;

    private int progresoactual1;
    private int progresoactual2;
    private int progresoactual3;

    private ExecutorService executor = Executors.newFixedThreadPool(3);
    private Future<?> tarea1, tarea2, tarea3;
    private boolean runningtarea1 = false;
    private boolean runningtarea2 = false;
    private boolean runningtarea3 = false;

    @FXML
    private void animateToView1(ActionEvent event) {
        UtilsViews.setViewAnimating("View1");
    }

    @FXML
    private void runTask(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();

        if (sourceButton == boton1) {
            tarea1 = toggleTask(0, boton1, tarea1, runningtarea1);
        } else if (sourceButton == boton2) {
            tarea2 = toggleTask(1, boton2, tarea2, runningtarea2);
        } else if (sourceButton == boton3) {
            tarea3 = toggleTask(2, boton3, tarea3, runningtarea3);
        }
    }

    private Future<?> toggleTask(int index, Button button, Future<?> task, boolean running) {
        if (running) {
            task.cancel(true);
            button.setText("Iniciar");
            updateRunningStatus(index, false);
            return null;
        } else {
            Future<?> newTask = backgroundTask(index, progresoactual(index));
            button.setText("Stop");
            updateRunningStatus(index, true);
            return newTask;
        }
    }

    private int progresoactual(int index) {
        switch (index) {
            case 0:
                return progresoactual1;
            case 1:
                return progresoactual2;
            case 2:
                return progresoactual3;
            default:
                return 0;
        }
    }

    private void updateRunningStatus(int index, boolean running) {
        switch (index) {
            case 0:
                runningtarea1 = running;
                break;
            case 1:
                runningtarea2 = running;
                break;
            case 2:
                runningtarea3 = running;
                break;
        }
    }

    private Future<?> backgroundTask(int index, int numprogreso) {
        final int finalvalor = numprogreso;

        return executor.submit(() -> {
            int valor = finalvalor;

            while (valor < 100) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                valor++;
                if (valor > 100) {
                    valor = 100;
                }
                final int currentValue = valor;

                Platform.runLater(() -> {
                    updateProgressBar(index, currentValue);
                    checkAndStop(index);
                });

                try {
                    Thread.sleep(getRandomSleepTime(index));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void updateProgressBar(int index, int value) {
        switch (index) {
            case 0:
                progresoactual1 = value;
                percentatge0.setText(String.valueOf(value) + "%");
                progress0.setProgress(value / 100.0);
                break;
            case 1:
                progresoactual2 = value;
                percentatge1.setText(String.valueOf(value) + "%");
                progress1.setProgress(value / 100.0);
                break;
            case 2:
                progresoactual3 = value;
                percentatge2.setText(String.valueOf(value) + "%");
                progress2.setProgress(value / 100.0);
                break;
        }
    }

    private void checkAndStop(int index) {
        if (progresoactual(index) == 99) {
            stopExecutor();
            updateRunningStatus(index, false);
        }
    }

    private int getRandomSleepTime(int index) {
        if (index == 0) {
            return 1000;
        } else if (index == 1) {
            return random.nextInt(3) + 3 * 1000;
        } else if (index == 2) {
            return (random.nextInt(6) + 3) * 1000;
        }
        return 0;
    }

    public void stopExecutor() {
        executor.shutdown();
    }
}
