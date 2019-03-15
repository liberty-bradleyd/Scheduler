package com.stratisapps.www.scheduler;

import android.content.Context;
import android.os.AsyncTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrganizeListTask{

    private ArrayList<ArrayList<String>> listOfEvents = null;
    private SimpleDateFormat originalFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

    protected ArrayList<ArrayList<String>> startProcess(ArrayList<ArrayList<String>>... arrayLists){
        listOfEvents = arrayLists[0];
        return mergeSort(listOfEvents);
    }

    public ArrayList<ArrayList<String>> mergeSort(ArrayList<ArrayList<String>> arr){
        if(arr.size() == 1){
            return  arr;
        }
        else{
            ArrayList<ArrayList<String>> left = new ArrayList<>();
            ArrayList<ArrayList<String>> right = new ArrayList<>();
            int middle = arr.size()/2;
            for(int i = 0; i < middle; i++){
                left.add(arr.get(i));
            }
            for(int j = middle; j < arr.size(); j++){
                right.add(arr.get(j));
            }
            left = mergeSort(left);
            right = mergeSort(right);
            merge(left, right, arr);
        }
        return arr;
    }

    public void merge(ArrayList<ArrayList<String>> left, ArrayList<ArrayList<String>> right, ArrayList<ArrayList<String>> arr){
        int leftPos = 0, rightPos = 0, arrPos = 0, extrasPos = 0;
        ArrayList<ArrayList<String>> extras;
        while(leftPos < left.size() && rightPos < right.size()){
            Date dateLeft = null;
            Date dateRight = null;
            try {
                dateLeft = originalFormat.parse(left.get(leftPos).get(3) + " " + left.get(leftPos).get(4);
                dateRight = originalFormat.parse(right.get(rightPos).get(3) + " " + right.get(rightPos).get(4));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(dateLeft.before(dateRight)){
                arr.set(arrPos, left.get(leftPos));
                leftPos++;
            }
            else if(dateLeft.after(dateRight)){
                arr.set(arrPos, right.get(rightPos));
                rightPos++;
            }
            else {
                if(left.get(leftPos).get(0).compareTo(right.get(rightPos).get(0)) < 0){
                    arr.set(arrPos, left.get(leftPos));
                    leftPos++;
                }
                else if(left.get(leftPos).get(0).compareTo(right.get(rightPos).get(0)) > 0){
                    arr.set(arrPos, right.get(rightPos));
                    rightPos++;
                }
                else {
                    if(left.get(leftPos).get(1).compareTo(right.get(rightPos).get(1)) < 0){
                        arr.set(arrPos, left.get(leftPos));
                        leftPos++;
                    }
                    else{
                        arr.set(arrPos, right.get(rightPos));
                        rightPos++;
                    }
                }
            }
            arrPos++;
        }
        if(leftPos >= left.size()){
            extras = right;
            extrasPos = rightPos;
        }
        else{
            extras = left;
            extrasPos = leftPos;
        }
        for(int i = extrasPos; i < extras.size(); i++){
            arr.set(arrPos, extras.get(i));
            arrPos++;
        }
    }
}
