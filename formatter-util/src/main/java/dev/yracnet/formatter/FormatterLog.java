/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.yracnet.formatter;

/**
 *
 * @author wyujra
 */
public interface FormatterLog {

    public void debug(String message);

    public void debug(String message, Throwable e);

    public void debug(Throwable e);

    public void warn(String message);

    public void warn(String message, Throwable e);

    public void warn(Throwable e);

    public void info(String message);

}
