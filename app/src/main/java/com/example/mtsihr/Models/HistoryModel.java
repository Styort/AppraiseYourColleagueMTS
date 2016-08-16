package com.example.mtsihr.Models;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Виктор on 15.08.2016.
 */
public class HistoryModel extends RealmObject {
    String partnership, efficiency, responsibility, courage, creativity, openness, name, dateOfEval, post, subdiv;

    public String getCourage() {
        return courage;
    }

    public String getName() {
        return name;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public void setSubdiv(String subdiv) {
        this.subdiv = subdiv;
    }

    public String getPost() {

        return post;
    }

    public String getSubdiv() {
        return subdiv;
    }

    public String getCreativity() {
        return creativity;
    }

    public void setCourage(String courage) {
        this.courage = courage;
    }

    public void setCreativity(String creativity) {
        this.creativity = creativity;
    }

    public void setDateOfEval(String dateOfEval) {
        this.dateOfEval = dateOfEval;
    }

    public void setEfficiency(String efficiency) {
        this.efficiency = efficiency;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOpenness(String openness) {
        this.openness = openness;
    }

    public void setPartnership(String partnership) {
        this.partnership = partnership;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    public String getDateOfEval() {
        return dateOfEval;
    }

    public String getEfficiency() {
        return efficiency;
    }

    public String getOpenness() {
        return openness;
    }

    public String getPartnership() {
        return partnership;
    }

    public String getResponsibility() {
        return responsibility;
    }
}
