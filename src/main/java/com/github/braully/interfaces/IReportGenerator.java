/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.braully.interfaces;

import com.github.braully.util.GenericDataReport;
import java.io.InputStream;

/**
 *
 * @author braully
 */
public interface IReportGenerator {

    public InputStream generate(GenericDataReport datareport);

}
