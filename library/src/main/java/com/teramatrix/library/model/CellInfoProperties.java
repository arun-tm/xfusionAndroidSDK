package com.teramatrix.library.model;

/**
 * Created by arun.singh on 10/7/2016.
 * Model to hold devcie service data like Network data , Phone data or Traffic data.
 */
public class CellInfoProperties {

    public String paramName;
    public String paramValue;
    public String type;
    public CellInfoProperties(String paramName, String paramValue,String type)
    {
        this.paramName = paramName;
        this.paramValue = paramValue;
        this.type = type;
    }

}
