/*
 * Copyright (C) 2022 rgt
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
package krakee.learn;

/**
 * Add export types
 */
public enum ExportType {
    OneCandle, //One Candle Row
    OneCandleBin, //One candle for binary classification

    HistCandle, //10 Hist candle in one row
    HistCCi //10 Hist CCi in one row
}
