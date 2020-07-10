package com.wac.android.finalscheduler.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class SharedViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private SharedViewModel sharedViewModel;

    private final Map<Class<? extends ViewModel>, ViewModel> mFactory = new HashMap<>();

    public SharedViewModelFactory(SharedViewModel vm) {
        this.sharedViewModel = vm;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(final @NonNull Class<T> mc) {
        mFactory.put(mc, sharedViewModel);

        if (SharedViewModel.class.isAssignableFrom(mc)) {
            SharedViewModel shareVM = null;

            if (mFactory.containsKey(mc)) {
                shareVM = (SharedViewModel) mFactory.get(mc);
            } else {
                try {
                    shareVM = (SharedViewModel) mc.getConstructor(Runnable.class).newInstance(new Runnable() {
                        @Override
                        public void run() {
                            mFactory.remove(mc);
                        }
                    });
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Error making instance of " + mc, e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error making an instance of " + mc, e);
                } catch (InstantiationException e) {
                    throw new RuntimeException("Error making an instance of " + mc, e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Error making an instance of " + mc, e);
                }
                mFactory.put(mc, shareVM);
            }

            return (T) shareVM;
        }
        return super.create(mc);
    }
}