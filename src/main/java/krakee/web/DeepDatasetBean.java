/*
 * Copyright (C) 2020 rgt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package krakee.web;

import deepnetts.data.TabularDataSet;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import krakee.deep.DeepEJB;

/**
 * Show dataset
 * @author rgt
 */
@SessionScoped
@Named(value = "deepDatasetBean")
public class DeepDatasetBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private TabularDataSet dataset;

    @EJB
    DeepEJB deepEjb;

    public List getDatasetRows() {
        if (dataset != null) {
            return dataset.getItems();
        }
        return null;
    }

    public Float getDatasetValue(Integer rowIdx, Integer colIdx) {
        if (dataset.getNumInputs() > colIdx) {
            return ((TabularDataSet.Item) dataset.get(rowIdx)).getInput().get(colIdx);
        } else {
            return ((TabularDataSet.Item) dataset.get(rowIdx)).getTargetOutput().get(colIdx-dataset.getNumInputs());
        }
    }

    public String[] getColumnNames() {
        if (dataset != null) {
            return dataset.getColumnNames();
        }
        return null;
    }

    public void setDataset(TabularDataSet dataset) {
        this.dataset = dataset;
    }
}
