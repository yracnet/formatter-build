/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.revelc.code.formatter;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author wyujra
 */
@Getter
@Setter
public class ResultCollector {

    private int successCount;
    private int failCount;
    private int skippedCount;
    private int readOnlyCount;

    public void reset() {
        successCount = 0;
        failCount = 0;
        skippedCount = 0;
        readOnlyCount = 0;
    }

    public void successCount() {
        successCount++;
    }

    public void failCount() {
        failCount++;
    }

    public void skippedCount() {
        skippedCount++;
    }

    public void readOnlyCount() {
        readOnlyCount++;
    }

    private long startClock, endClock;

    public void stop() {
        endClock = System.currentTimeMillis();
    }

    public long getTimeClock() {
        return ((endClock - startClock) / 1000);
    }

    public void start() {
        startClock = System.currentTimeMillis();
        endClock = startClock;
    }

}
