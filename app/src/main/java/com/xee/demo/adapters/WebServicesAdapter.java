/*
 * Copyright 2016 Eliocity
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xee.demo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xee.demo.R;
import com.xee.demo.interfaces.WebServices;

import java.util.List;

public class WebServicesAdapter extends RecyclerView.Adapter {

    /** List of Web services */
    private final List<WebServices.WS> items;

    /** Web service header */
    private static final int WS_HEADER = 0;
    /** Web service item */
    private static final int WS_ITEM = 1;

    /**
     * The list of Web services
     * @param items the web services
     */
    public WebServicesAdapter(List<WebServices.WS> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        WebServices.WS item = getItem(position);
        return item.isHeader() ? WS_HEADER : WS_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewType == WS_HEADER ? R.layout.cell_webservice_header : R.layout.cell_webservice, parent, false);
        return new WebServicesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((TextView) holder.itemView.findViewById(R.id.title)).setText(items.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    /**
     * Get the item from list of Web services
     * @param position the position to pick
     * @return the Web service
     */
    public WebServices.WS getItem(int position) {
        return items.get(position);
    }
}