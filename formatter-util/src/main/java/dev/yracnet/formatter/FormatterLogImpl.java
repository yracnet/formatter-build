/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.yracnet.formatter;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author wyujra
 */
class FormatterLogImpl implements FormatterLog {

    private static final Logger LOGGER = Logger.getLogger("dev.yracnet.formatter");

    public FormatterLogImpl() {
        InputStream stream = FormatterLogImpl.class.getClassLoader().getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void debug(String message) {
        LOGGER.log(Level.FINE, message);
    }

    @Override
    public void debug(String message, Throwable e) {
        LOGGER.log(Level.FINE, message, e);
    }

    @Override
    public void debug(Throwable e) {
        LOGGER.log(Level.FINE, "", e);
    }

    @Override
    public void warn(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    @Override
    public void warn(String message, Throwable e) {
        LOGGER.log(Level.WARNING, message, e);
    }

    @Override
    public void warn(Throwable e) {
        LOGGER.log(Level.WARNING, "", e);
    }

    @Override
    public void info(String message) {
        LOGGER.log(Level.INFO, message);
    }

}
