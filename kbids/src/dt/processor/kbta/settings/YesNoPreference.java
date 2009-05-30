package dt.processor.kbta.settings;

import android.content.*;
import android.content.res.*;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.*;
import android.util.*;

/**
 * The {@link YesNoPreference} is a preference to show a dialog with Yes and No
 * buttons.
 * <p>
 * This preference will store a boolean into the SharedPreferences.
 */
public class YesNoPreference extends DialogPreference {
    private static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        
        boolean wasPositiveResult;

        public SavedState(Parcel source) {
            super(source);
            wasPositiveResult = source.readInt() == 1;
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(wasPositiveResult ? 1 : 0);
        }
    }
    
    private boolean _wasPositiveResult;

    public YesNoPreference(Context context) {
        this(context, null);
    }
    
    public YesNoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YesNoPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Gets the value of this preference.
     * 
     * @return The value of the preference.
     */
    public boolean getValue() {
        return _wasPositiveResult;
    }
    
    /**
     * Sets the value of this preference, and saves it to the persistent store
     * if required.
     * 
     * @param value The value of the preference.
     */
    public void setValue(boolean value) {
        _wasPositiveResult = value;
        
        persistBoolean(value);
        
        notifyDependencyChange(!value);
    }
    
    @Override
    public boolean shouldDisableDependents() {
        return !_wasPositiveResult || super.shouldDisableDependents();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (callChangeListener(positiveResult)) {
            setValue(positiveResult);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getBoolean(index, false);
    }
    
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }
         
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setValue(myState.wasPositiveResult);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }
        
        final SavedState myState = new SavedState(superState);
        myState.wasPositiveResult = getValue();
        return myState;
    }
    
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedBoolean(_wasPositiveResult) :
            (Boolean) defaultValue);
    }
    
}
