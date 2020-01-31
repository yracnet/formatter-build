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
public class FormatterException extends Exception{

    public FormatterException(String message) {
        super(message);
    }

    public FormatterException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
