package com.quovantis.common.helpers;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * An Interface contract for managing views in {@link BaseRecyclerAdapter}.
 *
 * @param <D> Type of Data object the View represents.
 * @param <V> Type of View object managed.
 * @see BaseRecyclerAdapter#register(Class, ItemManager)
 * @see BaseRecyclerAdapter#unRegister(Class)
 */
public interface ItemManager<D, V extends View> {

    /**
     * Called when a new instance of view is to be created.
     *
     * @param context context.
     * @param parent  Parent ViewGroup.
     * @return View instance.
     */
    public V createView(Context context, ViewGroup parent);

    /**
     * Called when a data object is to be bound to a View instance.
     *
     * @param view       View instance.
     * @param data       Data Object.
     * @param position   Position in Adapter.
     * @param itemHolder RecyclerView holder object for View.
     * @param adapter    BaseAdapter for extra querying or operations.
     */
    public void bindData(V view, D data, int position, ItemHolder itemHolder, BaseRecyclerAdapter adapter);
}
