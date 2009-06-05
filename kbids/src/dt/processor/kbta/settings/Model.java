package dt.processor.kbta.settings;

import static android.text.TextUtils.isEmpty;
import static dt.processor.kbta.Env.TAG;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.os.Environment;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.widget.Toast;
import dt.processor.kbta.Env;
import dt.processor.kbta.util.Pair;

public abstract class Model{
	private static final File SDCARD = Environment.getExternalStorageDirectory();

	private final DateFormat DF = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

	/** The name of the threat assessments xml file */
	public static final String THREAT_ASSESSMENTS = "threat_assessments.xml";

	/** The name of the ontology xml file */
	public static final String ONTOLOGY = "ontology.xml";

	private Preference _details;

	private ListPreference _load;

	private DialogPreference _reset;

	private final File _modelFile;

	private final String _modelName, _xmlRootTag;

	private final SettingsScreen _settingsScreen;

	private final PreferenceCategory _monitoredThreats;

	private final boolean _isServiceRunning;

	Model(SettingsScreen settingsScreen, PreferenceCategory monitoredThreats,
		File modelFile, String modelName, String xmlRootTag, boolean isServiceRunning){
		_settingsScreen = settingsScreen;
		_monitoredThreats = monitoredThreats;
		_modelFile = modelFile;
		_modelName = modelName;
		_xmlRootTag = xmlRootTag;
		_isServiceRunning = isServiceRunning;
	}

	public void initPrefs(PreferenceScreen root){

		PreferenceCategory category = new PreferenceCategory(_settingsScreen);
		category.setTitle(_modelName);
		root.addPreference(category);

		// Setting the initial details that will be amended after the initialization
		_details = new Preference(_settingsScreen, null,
				android.R.attr.preferenceInformationStyle);
		_details.setTitle("Name: -");
		_details.setSummary("Last updated: -");
		category.addPreference(_details);

		if (!_isServiceRunning){
			initLoadPref(category);
			initResetPref(category);
		}
	}

	private void initLoadPref(PreferenceCategory category){
		_load = new ListPreference(_settingsScreen);
		_load.setPersistent(false);
		_load.setTitle("Load " + _modelName + " From SD-Card");
		_load.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			public boolean onPreferenceChange(Preference preference, Object newValue){
				try{
					// Copying the model file from the SD-Card to the private storage
					Model.copyModelFile(_settingsScreen, _modelFile, new FileInputStream(
							new File(SDCARD, (String)newValue)));
					// Performing the actual loading
					loadModel();
				}catch(IOException e){
					Log.e(TAG, "Unable to load \"" + newValue + "\" due to: "
							+ e.getMessage(), e);
					Toast.makeText(_settingsScreen,
						"Unable to load the requested " + _modelName, Toast.LENGTH_SHORT)
							.show();

					// Reloading potential models just in case
					refreshModelsFromOnSdCard();
				}
				return true;
			}
		});
		refreshModelsFromOnSdCard();
		category.addPreference(_load);
	}

	/** Loads (parses) the appropriate model from the file */
	public void loadModel(){
		// Preparing the progress dialog
		Env.LoadingCallback callback = new Env.LoadingCallback(){
			@Override
			public void onSuccess(){
				_settingsScreen.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						refreshDetails();
						if (_settingsScreen.areModelsCompatibile()){
							try{
								onCompatibleModelLoading();
							}catch(IllegalStateException e){
								onFailure(e);
							}
						}else{
							onIncompatibleModelLoading();
						}
					}
				});
			}

			@Override
			public void onFailure(final Throwable t){
				_settingsScreen.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						String dueTo;
						int length;
						if (t == null){
							dueTo = "";
							length = Toast.LENGTH_SHORT;
						}else{
							dueTo = " due to: " + t.getMessage();
							length = Toast.LENGTH_LONG;
						}
						String message = "Unable to load the requested " + _modelName
								+ dueTo + ", reverting to default";
						Toast.makeText(_settingsScreen, message, length).show();
						if (t == null){
							Log.e(TAG, message);
						}else{
							Log.e(TAG, message, t);
						}

						// Rollback, must not fail
						Model.copyDefaultModelFile(_settingsScreen, _modelFile);
						loadModel(_settingsScreen, null);
					}
				});
			}
		};

		loadModel(_settingsScreen, callback); // Do the actual loading
	}

	private void onCompatibleModelLoading() throws IllegalStateException{
		// Setting the initial monitored state of all elements
		// according to the new threat assessments
		Env.getThreatAssessor().setInitiallyMonitoredThreats(Env.getOntology());

		// Refreshing the monitored threats preferences
		_monitoredThreats.removeAll();
		_settingsScreen.initMonitoredThreatsPrefs();
		Toast.makeText(_settingsScreen, "Succesfully loaded the " + _modelName,
			Toast.LENGTH_SHORT).show();
	}

	private void onIncompatibleModelLoading(){
		Toast.makeText(
			_settingsScreen,
			"Warning! Loaded models are not compatible. "
					+ "If not changed, they will be reset to default upon exit",
			Toast.LENGTH_LONG).show();

		// Adding an error preference to the monitored threats category
		_monitoredThreats.removeAll();
		Preference errorPref = new Preference(_settingsScreen, null,
				android.R.attr.preferenceInformationStyle);
		errorPref.setTitle("Incompatible models");
		errorPref.setSummary("Threats cannot be monitored");
		_monitoredThreats.addPreference(errorPref);
	}

	public abstract void loadModel(Context context, Env.LoadingCallback callback);

	private void initResetPref(PreferenceCategory category){
		String lowerCaseName = _modelName.toLowerCase();
		_reset = new YesNoPreference(_settingsScreen);
		String title = "Reset " + _modelName + " To Default";
		_reset.setTitle(title);
		_reset.setDialogTitle(title);
		_reset.setDialogMessage("Are you sure you want to revert to the default "
				+ lowerCaseName + "?");
		_reset.setPositiveButtonText("Yes");
		_reset.setNegativeButtonText("No");
		_reset.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			public boolean onPreferenceChange(Preference preference, Object newValue){
				if (Boolean.TRUE.equals(newValue)){
					if (Model.copyDefaultModelFile(_settingsScreen, _modelFile)){
						// Performing the actual loading
						loadModel();
						_load.setValue(null);
					}else{
						Toast.makeText(_settingsScreen,
							"Unable to load the default " + _modelName,
							Toast.LENGTH_SHORT).show();
					}
				}
				return true;
			}
		});
		category.addPreference(_reset);
	}

	public void refreshDetails(){
		String nameAndVersion = getNameAndVersionOfCurrentlyLoadedModel();
		_details.setTitle("Name: " + nameAndVersion);

		String lastUpdated = _modelFile.exists() ? DF.format(_modelFile.lastModified())
				: "-";
		_details.setSummary("Last updated: " + lastUpdated);
	}

	private void refreshModelsFromOnSdCard(){
		Pair<String[], String[]> namesAndModelFiles = findModelFilesOnSDCard();
		_load.setEntries(namesAndModelFiles.first); // The names will be displayed
		_load.setEntryValues(namesAndModelFiles.second); // The values are the
		// files
	}

	public abstract String getNameOfCurrentlyLoadedModel();

	public abstract String getNameAndVersionOfCurrentlyLoadedModel();

	private Pair<String[], String[]> findModelFilesOnSDCard(){
		final List<String> names = new ArrayList<String>();

		String[] files = SDCARD.list(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String filename){
				int lastIndexOfDot = filename.lastIndexOf(".");
				if (lastIndexOfDot >= 0
						&& filename.substring(lastIndexOfDot).equalsIgnoreCase(".xml")){
					return hasMatchingRootTag(new File(dir, filename));
				}
				return false;
			}

			private boolean hasMatchingRootTag(File file){
				try{
					XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
					XmlPullParser xpp = factory.newPullParser();
					xpp.setInput(new FileReader(file));
					for (int et = xpp.getEventType(); et != END_DOCUMENT; et = xpp.next()){
						if (et == START_TAG){
							if (_xmlRootTag.equalsIgnoreCase(xpp.getName())){
								String name = xpp.getAttributeValue(null, "name");
								String version = xpp.getAttributeValue(null, "version");
								if (!isEmpty(name) && !isEmpty(version)){
									// Adding the display name to the names list
									names.add(name + " V." + version);
									return true;
								}
							}else{
								return false;
							}
						}
					}
				}catch(Exception e){}

				return false;
			}
		});
		return new Pair<String[], String[]>(names.toArray(new String[names.size()]),
				files);
	}

	/**
	 * Copies the default model to the given destination
	 * 
	 * @return Whether the copy was successful
	 */
	public static boolean copyDefaultModelFile(Context context, File destination){
		try{
			Model.copyModelFile(context, destination, context.getAssets().open(
				destination.getName()));
			return true;
		}catch(IOException e){
			Log.e(TAG, "Unable to copy default: " + destination.getName(), e);
			return false;
		}
	}

	static void copyModelFile(Context context, File destination, InputStream src)
			throws IOException{
		final File bak = new File(destination.getAbsoluteFile() + ".bak");
		final boolean exists = destination.exists();

		// Backing up the existing model if one exists
		if (exists){
			destination.renameTo(bak);
		}
		OutputStream os = null;
		try{
			// Copying the new model to the destination location
			os = context.openFileOutput(destination.getName(), 0);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = src.read(buf)) > 0){
				os.write(buf, 0, bytesRead);
			}

			// Deleting the backup
			if (exists){
				bak.delete();
			}
		}catch(IOException e){
			// Reverting to backup if possible
			if (exists){
				bak.renameTo(destination);
			}
			throw e;
		}finally{
			// Closing the source input stream
			try{
				src.close();
			}catch(IOException ee){};

			// Closing the destination output stream
			try{
				if (os != null){
					os.close();
				}
			}catch(IOException ee){};
		}
	}

	public static File getThreatsModelFile(Context context){
		return new File(context.getFilesDir(), THREAT_ASSESSMENTS);
	}

	public static File getOntologyModelFile(Context context){
		return new File(context.getFilesDir(), ONTOLOGY);
	}
}
