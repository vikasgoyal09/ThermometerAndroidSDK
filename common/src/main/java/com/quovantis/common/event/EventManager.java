package com.quovantis.common.event;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Event manager will provide functionality to register for event you can simply
 * Use getInstance method and register and unregister the Receiver on broadcast manager
 * </p>
 * <p>
 * To register a event by action type you need to use #registerForEvent method and provide
 * callback implementation for {@link EventReceivedListener}
 * on event received for define action type {@link EventReceivedListener#onEventReceived(Object)} method will get called
 * </p>
 * <p>
 * If you register for a event then you should also call unregister for that event also
 * by calling {@link EventManager#unregisterReceiver(String, EventReceivedListener)} else it
 * will leak the receiver reference which is not a best practice
 * </p>
 * <p/>
 * <p>
 * To Broadcast a event call {@link EventManager#broadcastEvent(String, Object)} method by passing event
 * type in argument and data for event listener
 * </p>
 *
 * @see EventManager.EventReceivedListener
 */
public class EventManager {

    /**
     * Singleton instance
     */
    private static EventManager sInstance;

    /**
     * Collection will keep list of callback listener on basis of event type
     */
    private HashMap<String, List<EventReceivedListener>> mEventCallbacksMap = new HashMap<String, List<EventReceivedListener>>(5);
    /**
     * Collection will keep mapped data for define action type
     */
    private HashMap<String, Object> mEventDataMap = new HashMap<String, Object>(5);

    /**
     * To post message on main thread use this Handler reference
     */
    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * Private constructor for single ton instance only
     */
    private EventManager() {
        mEventCallbacksMap.clear();
        mEventDataMap.clear();
    }

    /**
     * Create Single ton instance for class
     *
     * @return EventManager Reference
     */
    synchronized public static EventManager getInstance() {
        if (null == sInstance) {
            sInstance = new EventManager();
        }
        return sInstance;
    }

    /**
     * <p>
     * Register a action here method will first search action in {@link #mEventCallbacksMap}
     * if it is found then add callback reference in list of map for the key
     * If not found then create a new list for the action and callback in
     * that list and put that list in {@link #mEventCallbacksMap} with key action
     * </p>
     *
     * @param action   Action which need to register
     * @param callback Callback listener for the action
     */
    public synchronized void registerForEvent(String action, EventReceivedListener callback) {
        if (callback == null) {
            return;
        }
        List<EventReceivedListener> registerCallback = mEventCallbacksMap.get(action);
        //check whether the action callback list is already maintained if not then create and put in map
        if (null == registerCallback) {
            registerCallback = new ArrayList<EventReceivedListener>();
            mEventCallbacksMap.put(action, registerCallback);
        }
        //if callback list for event not having callback already in list then add it in the list
        if (!registerCallback.contains(callback)) {
            registerCallback.add(callback);
        }
    }

    /**
     * <p>
     * Unregister the callback listener for define action from here
     * it will first find the associated list of callback for the action
     * from {@link #mEventCallbacksMap} map
     * remove callback listener from that list if that list is empty then
     * remove that action from #mEventCallbacksMap map and find and remove the associated
     * <p/>
     * and then unregister that received receiver from LocalBroadCastManager
     * </p>
     *
     * @param action   Action which need to register
     * @param callback Callback listener for the action
     */
    public synchronized void unregisterReceiver(String action, EventReceivedListener callback) {
        if (callback == null) {
            return;
        }
        List<EventReceivedListener> registerCallback = mEventCallbacksMap.get(action);
        if (null != registerCallback) {
            registerCallback.remove(callback);
            if (registerCallback.isEmpty()) {
                mEventCallbacksMap.remove(action);
            }
        }
    }

    /**
     * Broadcast event from here by passing action for intent
     *
     * @param action Action
     * @param data   event data which need to be pass on event received
     */
    public void broadcastEvent(String action, Object data) {
        if (action == null) {
            return;
        }
        if (!TextUtils.isEmpty(action.trim())) {
            if (null != data) {
                mEventDataMap.put(action, data);
            }
            fireEventOnSubscriber(action);
        }
    }

    /**
     * Method will fire event associated data on callback listener
     *
     * @param action Event action type
     */
    private synchronized void fireEventOnSubscriber(String action) {
        if (action == null) {
            return;
        }
        if (!TextUtils.isEmpty(action.trim())) {
            List<EventReceivedListener> registerCallback = mEventCallbacksMap.get(action);
            final Object eventData = mEventDataMap.remove(action);
            if (null != registerCallback) {
                for (final EventReceivedListener receiver : registerCallback) {
                    try {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                receiver.onEventReceived(eventData);
                            }
                        });
                    } catch (ClassCastException e) {
                        Log.i("EventManager", "Typed data not matched with broadcasting data");
                        //In case of exception with data value then only notify for event
                        receiver.onEventReceived(null);
                    }
                }
            }
        }
    }

    /**
     * Check whether the subscriber for the provided action is available or not
     *
     * @param action String action type
     * @return true if subscriber for event available else return false
     */
    public boolean hasSubscriber(String action) {
        List<EventReceivedListener> listeners = mEventCallbacksMap.get(action);
        return null != listeners && !listeners.isEmpty();
    }

    /**
     * Callback listener for register class on received
     * broadcasts events
     */
    public interface EventReceivedListener<T> {
        /**
         * Method will be get called for register listeners in {@link EventManager}
         * on received broadcast event
         *
         * @param data event data
         */
        void onEventReceived(T data) throws ClassCastException;
    }

}
