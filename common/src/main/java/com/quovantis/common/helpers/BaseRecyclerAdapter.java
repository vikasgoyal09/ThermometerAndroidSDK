package com.quovantis.common.helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.*;

/**
 * Utility adapter for {@link RecyclerView} that can accommodate multiple types of data
 * and corresponding views, with help of registered {@link ItemManager} instances.
 */
public class BaseRecyclerAdapter extends RecyclerView.Adapter<ItemHolder> {
    private final Map<Integer, Class> mTypeMap;
    private final Map<Class, ItemManager> mManagerMap;
    protected final List<Object> mDataList;
    private final Context mContext;

    /**
     * Create a new instance.
     *
     * @param context context.
     */
    public BaseRecyclerAdapter(Context context) {
        mContext = context;
        mTypeMap = new HashMap<Integer, Class>();
        mManagerMap = new HashMap<Class, ItemManager>();
        mDataList = new ArrayList<Object>();
    }

    /**
     * Register a View manager with adapter. This is necessary before adding objects of a new
     * data type objects to adapter.
     *
     * @param dataClass   class of adapter data objects.
     * @param itemManager view manager to handle UI for objects of data class being registered.
     * @param <D>         Type of data objects being registered.
     */
    public <D> void register(Class<D> dataClass, ItemManager<D, ? extends View> itemManager) {
        if (mManagerMap.containsKey(dataClass)) {
            mManagerMap.put(dataClass, itemManager);
            return;
        }

        mTypeMap.put(dataClass.hashCode(), dataClass);
        mManagerMap.put(dataClass, itemManager);
    }

    /**
     * Unregister a data object class.
     * <b>Warning: this will also remove all present objects of this class type from adapter.</b>
     *
     * @param dataClass data class type to unregister.
     */
    public void unRegister(Class<?> dataClass) {
        if (mTypeMap.containsKey(dataClass.hashCode())) {

            mTypeMap.remove(dataClass.hashCode());
            mManagerMap.remove(dataClass);

            Iterator<Object> iterator = mDataList.iterator();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (obj.getClass().equals(dataClass)) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Get context reference.
     *
     * @return context.
     */
    public Context getContext() {
        return mContext;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemManager itemManager = mManagerMap.get(mTypeMap.get(viewType));
        return new ItemHolder(itemManager.createView(mContext, parent));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int position) {
        Object data = getItemAt(position);
        ItemManager itemManager = mManagerMap.get(data.getClass());
        itemManager.bindData(itemHolder.itemView, data, position, itemHolder, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemViewType(int position) {
        return getItemAt(position).getClass().hashCode();
    }

    /**
     * Get item at specified position in adapter.
     *
     * @param position position in adapter.
     * @return Data object found at the position.
     */
    public Object getItemAt(int position) {
        return mDataList.get(position);
    }

    /**
     * Add a data object to adapter.
     *
     * @param obj data object to add.
     * @throws IllegalArgumentException If type of object is not already registered with adapter.
     */
    public void add(Object obj) {
        if (mTypeMap.containsKey(obj.getClass().hashCode())) {
            mDataList.add(obj);
        } else {
            throw new IllegalArgumentException("ViewManager not registered for object type: " + obj.getClass());
        }
    }

    /**
     * Add a data object to adapter, at specified position.
     *
     * @param position position in adapter.
     * @param obj      data object to add.
     * @throws IllegalArgumentException If type of object is not already registered with adapter.
     */
    public void add(int position, Object obj) {
        if (mTypeMap.containsKey(obj.getClass().hashCode())) {
            mDataList.add(position, obj);
        } else {
            throw new IllegalArgumentException("ViewManager not registered for object type: " + obj.getClass());
        }
    }

    /**
     * Add all given data objects to adapter.
     *
     * @param objects data objects to add.
     * @throws IllegalArgumentException If type of an object is not already registered with adapter.
     */
    public void addAll(Collection<?> objects) {
        for (Object obj : objects) {
            add(obj);
        }
    }

    /**
     * Remove a data object present at given position from adapter.
     *
     * @param position position in adapter.
     * @return removed object.
     */
    public Object remove(int position) {
        return mDataList.remove(position);
    }

    /**
     * Remove all data objects from adapter.
     */
    public void clear() {
        mDataList.clear();
    }

    /**
     * Check whether object at specified position is of given type.
     *
     * @param position  position in adapter.
     * @param dataClass class type to check.
     * @return true if object at specified position is of given type, false otherwise.
     */
    public boolean isItemOfType(int position, Class<?> dataClass) {
        return dataClass.equals(getItemAt(position).getClass());
    }

    /**
     * Move a data object to a new position in adapter.
     *
     * @param oldPosition existing position in adapter.
     * @param newPosition new position in adapter.
     */
    public void move(int oldPosition, int newPosition) {
        mDataList.add(newPosition, mDataList.remove(oldPosition));
    }

    /**
     * Get positions and objects for given data class, using linear search.
     *
     * @param dataClass registered class.
     * @return a Map of positions and data objects of given class in adapter.
     */
    public Map<Integer, Object> getItemsOfDataType(Class<?> dataClass) {
        Map<Integer, Object> positionObjectMap = new HashMap<Integer, Object>();
        for (int i = 0; i < getItemCount(); i++) {
            Object obj = getItemAt(i);
            if (dataClass.equals(obj.getClass())) {
                positionObjectMap.put(i, obj);
            }
        }
        return positionObjectMap;
    }

    /**
     * Do a linear search for position of first matching object of given data class, nearest to
     * give starting position (inclusive).
     *
     * @param dataClass     class type to search.
     * @param startPosition starting position. Output can be starting position as well.
     * @param searchUp      true to search on of before start position, false to search on or after
     *                      start position.
     * @return nearest position of object with given data type, or -1 if not found.
     */
    public int findNearestPositionOfType(Class dataClass, int startPosition, boolean searchUp) {

        for (int i = startPosition; i < getItemCount() && i >= 0; i += searchUp ? -1 : 1) {
            if (dataClass.equals(getItemAt(i).getClass())) {
                return i;
            }
        }

        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
