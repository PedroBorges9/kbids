package dt.processor.kbta.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class FileChangeTracker{
	private static final String SHARED_PREFERENCES_NAME = "file_change_tracker";

	/** The single instance */
	private static FileChangeTracker _instance;

	private final SharedPreferences _prefs;

	private FileChangeTracker(Context context){
		_prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME,
			Context.MODE_PRIVATE);
	}

	/**
	 * Checks whether the given asset file has been modified since the last tracking
	 * 
	 * @param context Context reference
	 * @param fileId The id of the file (unique identifier for the FileChangeTracker)
	 * @param assetName The name of the asset file
	 * @return Information regarding the change if one has occurred
	 * @throws IOException If the asset can not be opened
	 */
	public synchronized ChangeInfo hasBeenModified(Context context, String fileId,
		String assetName) throws IOException{
		String savedHash = _prefs.getString(fileId, null);
		String currentHash = md5Hash(readFully(context.getAssets().open(assetName)));
		boolean hasntBeenModified = (savedHash != null && savedHash.equals(currentHash));
		return new ChangeInfo(hasntBeenModified, savedHash == null, currentHash, fileId);
	}

	/**
	 * Updates the status of the file in the FileChangeTracker's data (saves the current
	 * hash)
	 * 
	 * @param ci The ChangeInfo object obtained from a previous call to hasBeenModified
	 */
	public synchronized void updateFileStatus(ChangeInfo ci){
		Editor editor = _prefs.edit();
		editor.putString(ci.fileId, ci.currentHash);
		editor.commit();
	}

	/**
	 * Updates the status of the file in the FileChangeTracker's data (saves the current
	 * hash)
	 * 
	 * @param ci The ChangeInfo object obtained from a previous call to hasBeenModified
	 * @throws IOException 
	 */
	public synchronized void updateFileStatus(String fileId, File file)
			throws IOException{
		String currentHash = md5Hash(readFully(new FileInputStream(file)));
		Editor editor = _prefs.edit();
		editor.putString(fileId, currentHash);
		editor.commit();
	}

	/**
	 * A getter for the single instance.
	 */
	public static FileChangeTracker getFileChangeTracker(Context context){
		if (_instance == null){
			_instance = new FileChangeTracker(context);
		}

		return _instance;
	}

	/**
	 * The tearing down of the singleton instance.
	 */
	public static void tearDown(){
		_instance = null;
	}

	public static class ChangeInfo{
		/**
		 * Whether the file has not been tracked before or it has been but it's current
		 * hash is different from the one that is saved
		 */
		public final boolean hasntBeenModified;

		/** Whether the file has not been tracked before */
		public final boolean firstTimeTracked;

		/** The current hash of the file */
		final String currentHash;

		/** The id of the file that is used as a key into the shared preferences */
		final String fileId;

		ChangeInfo(boolean hasntBeenModified, boolean firstTimeTracked,
			String currentHash, String fileId){
			this.hasntBeenModified = hasntBeenModified;
			this.firstTimeTracked = firstTimeTracked;
			this.currentHash = currentHash;
			this.fileId = fileId;
		}
	}

	/**
	 * Hashes the given bytes using MD5 and returns a 32 hex characters representing the
	 * hash
	 * 
	 * @param bytes The bytes to be hashed
	 * @return The hash (32 hex chars)
	 */
	private static String md5Hash(byte[] bytes){
		MessageDigest md;

		try{
			md = MessageDigest.getInstance("MD5");
		}catch(NoSuchAlgorithmException e){
			System.err.println("MD5 is not supported");
			return null;
		}

		// Hashing
		md.update(bytes);

		return byteArrayToHexString(md.digest());
	}

	/**
	 * Reads the contents of the resource into a byte array
	 * 
	 * @param context Context reference
	 * @return A byte array with the contents of the resource
	 */
	private static byte[] readFully(InputStream in) throws IOException{
		int bytesRead;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bytes = new byte[1024];
		while ((bytesRead = in.read(bytes)) >= 0){
			baos.write(bytes, 0, bytesRead);
		}
		in.close();

		bytes = baos.toByteArray();

		return bytes;
	}

	private static String byteArrayToHexString(byte[] bytes){
		StringBuilder sb = new StringBuilder();
		String hex;

		for (byte b : bytes){
			if ((hex = Integer.toHexString(b & 0xff)).length() == 1){
				sb.append("0");
			}
			sb.append(hex);
		}

		return sb.toString();
	}
}
