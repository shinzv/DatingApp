package eulberg.datingapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment  {

    //For taking a picture.
    private static final String TITLE = "Peer";
    private static final String DESCRIPTION = "Picture was took with peer!";

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final int IMAGE_CAPTURE = 1;

    //Views
    private ImageView profilePicture;
    private TextView nameAndAge;
    private TextView description;

    //Over this URI is the image accesable
    private Uri imageURI;

    //Galerie Auswahl
    private static final int RQ_GALLERY_PICK = 2;
    private static final String[] apps = new String[3];

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    //For storing the image in the storage -> the image is referenced to a user.
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    //UserData
    private UserSettings userSettings;
    private String userID;

    //SharedPreferences
    public static final String SHARED_PREFS = "SharedPrefs";

    public static final String uriImg= "URI";

    /**
     * Siehe „Lifecyle of Activity“ für den Aufrufszeitraum.
     * inflates the fragment profile
     * @param savedInstanceState
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    /**
     * Siehe „Lifecyle of Activity“ für den Aufrufszeitraum.
     * Initialisierungen usw...
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Initializing views
        nameAndAge = getView().findViewById(R.id.name);
        description = getView().findViewById(R.id.profileDescription);
        Button editButton = getView().findViewById(R.id.editButton);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInternetConnection()) {
                    Intent editProfileIntent = new Intent(getActivity(), EditProfile.class);
                    //Übergabe des Objekts als Byte-Stream.
                    editProfileIntent.putExtra("serialized_data_user_settings", userSettings);

                    startActivity(editProfileIntent);
                }else{
                    Toast.makeText(getContext(), "Keine Internet Verbindung!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button logoutButton = getView().findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();

                startActivity(new Intent(getActivity(), Login.class));

            }
        });




        //Initializing Apps Array for the AlertDialog
        apps[0] = "Kamera";
        apps[1] = "Galerie";
        apps[2] = "Bild entfernen";

        profilePicture = getView().findViewById(R.id.profile_picture);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInternetConnection()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("App auswählen...")
                            .setItems(apps, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // The 'which' argument contains the index position
                                    // of the selected item
                                    switch (which) {
                                        case 0:
                                            startCamera();
                                            break;
                                        case 1:
                                            startGalery();
                                            break;
                                        case 2:
                                            removePicture();
                                    }

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else{
                    Toast.makeText(getContext(), "Keine Internet Verbindung!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fireBaseAuth();

        //Animiert den  Hintergrund des Profilbildes
        RelativeLayout profile = getView().findViewById(R.id.profileBackground);
        AnimationDrawable animationDrawable = (AnimationDrawable) profile.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        //Aktualisiere das Profilbild.
        loadProfileFiles();
    }

    /**
     * Startet die Galarie und lässt den Benutzer ein Bild auswählen, das dann zu seinem Profilbild wird.
     */
    private void startGalery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RQ_GALLERY_PICK);
    }


    /**
     * startet die vorgefertigte Kamera-Activity und erhät ein Ergebnis.
     */
    private void startCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, TITLE);
        values.put(MediaStore.Images.Media.DESCRIPTION, DESCRIPTION);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        imageURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    /**
     * Löscht das Bild aus der Datenbank, den SharedPrefs und der profile picture view
     */
    private void removePicture() {
        try {
            storageReference.child("ProfilePictures/" + mAuth.getCurrentUser().getUid()).delete();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(uriImg);
            editor.apply();
            //TODO Ursprungsbild muss direkt angezeigt werden und nicht erst wenn man das Fragment switcht -> checked!
            profilePicture.setImageResource(R.drawable.peer_avi);
            Toast.makeText(getContext(), "Bild entfernt", Toast.LENGTH_LONG).show();
        }catch (NullPointerException n){
            Toast.makeText(getContext(), "Kein Bild ausgewählt", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Wird aufgerufen, nachdem das Ergebnis einer Activity zurückkommt. Das Ergebnis wird hier empfangen und verarbeitet.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //Kamera
        if(requestCode == IMAGE_CAPTURE){
            if(resultCode == RESULT_OK){
                try{
                    //Hier wird kein Try catch gebraucht, da das Bild neu aufgenommen wird.
                    Bitmap b1 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageURI);

                    //Größe des aufgenommenen Bildes für die eventuelle Skalierung
                    float w1 = b1.getWidth();
                    float h1 = b1.getHeight();
                    //skalierung auf 720p
                    int h2 = 720;
                    int w2 = (int)(w1 / h1 * (float) h2);
                    Bitmap scaledBitmap = b1.createScaledBitmap(b1, w2, h2, false);
                    imageURI = getImageUri(getContext(), scaledBitmap);

                    profilePicture.setImageBitmap(scaledBitmap);

                    saveProfilePicture();
                }catch (IOException e) { //und FileNotFoundException
                    e.printStackTrace();
                }
            }else{
                int rowsDeleted = getActivity().getContentResolver().delete(imageURI, null, null);
                Log.d(TAG, rowsDeleted + " rows deleted");
            }
        }else if (requestCode == RQ_GALLERY_PICK){ //Galerie
            if(resultCode == RESULT_OK){
                //Data is a intent
                if(data != null){
                    imageURI = data.getData();

                    try {
                        Bitmap b1 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageURI);

                        //Größe des aufgenommenen Bildes für die eventuelle Skalierung
                        float w1 = b1.getWidth();
                        float h1 = b1.getHeight();
                        //skalierung auf 720p
                        int h2 = 720;
                        int w2 = (int)(w1 / h1 * (float) h2);
                        Bitmap scaledBitmap = b1.createScaledBitmap(b1, w2, h2, false);
                        imageURI = getImageUri(getContext(), scaledBitmap);
                        profilePicture.setImageBitmap(scaledBitmap);

                        saveProfilePicture();
                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }


                }
            }

        }else{
            getActivity().finish();
        }


    }

    /**
     * Speichert das Profilbild lokal und auf dem Server ab.
     */
    private void saveProfilePicture(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(uriImg, imageURI.toString());
        editor.apply();

        //Saving the image on the server
        FirebaseUser user = mAuth.getCurrentUser();


        uploadImage();

    }

    /**
     * Speichert das Profilbild auf dem Server ab.
     */
    private void uploadImage(){
        if(imageURI != null){

            try {
                Bitmap b1 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageURI);

                //Größe des aufgenommenen Bildes für die eventuelle Skalierung
                float w1 = b1.getWidth();
                float h1 = b1.getHeight();
                //skalierung auf 720p
                int h2 = 720;
                int w2 = (int)(w1 / h1 * (float) h2);
                Bitmap scaledBitmap = b1.createScaledBitmap(b1, w2, h2, false);

            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }


            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            //Es wird unter dem Verzeichnis "ProfilePictures/ " userId abgespeichert um jeden user sein Profilbild zuzuordnen.

            storageReference.child("ProfilePictures/" + userID ).putFile(imageURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //Prozentangabe: Wie viel bereits verschickt wurde.
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded : " + progress + "%");
                        }
                    });
        }
    }

    //------------------------------------------------------
    //Konvertierung von Bitmaps in Uris
    //------------------------------------------------------

    /**
     * Die übergebene Bitmap wird in eine Uri umgewandelt und zurückgegeben.
     * @param inContext
     * @param inImage
     * @return
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //----------------------------------------------------------------------
    //Konvertierung von Bitmaps in Uris beendet
    //----------------------------------------------------------------------

    /**
     * lädt die Uri des Profilbilds aus den SharedPrefs Daten aus und lädt es in die CicleImageView.
     */
    private void loadProfileFiles(){
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        //Uri ist abstract und kann nicht instanziiert werden.
        imageURI = Uri.parse(sharedPreferences.getString(uriImg, ""));
        if(imageURI.toString() != "") {
            try {
                Bitmap b1 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageURI);
                profilePicture.setImageBitmap(b1);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        } else {

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePictures/"+userID);

            long megabyte = 1024 * 1024;
            storageReference.getBytes(megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(final byte[] bytes) {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory(), "ProfilePicture " + userID);

                        FileOutputStream fos = new FileOutputStream(file.getPath());
                        fos.write(bytes);
                        fos.close();

                        imageURI = Uri.fromFile(file);
                        profilePicture.setImageURI( imageURI);
                        sharedPreferences.edit().putString(uriImg, imageURI.toString()).apply();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });

            /*
            //Wenn kein Bild in den Prefs existiert, dann downloade es vom Server.
            FirebaseStorage localStorage = FirebaseStorage.getInstance();
            // Create a reference to a file from a Google Cloud Storage URI
            StorageReference gsReference = localStorage.getReference();
            StorageReference ref = gsReference.child("ProfilePictures/zrTMhCSqvFPAh1olCcHtyTMoBTh2.jpeg");

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePictures/"+userID);
            long megabyte = 1024 * 1024;
            storageReference.getBytes(megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Glide.with(ProfileFragment.this).asBitmap().load(bytes).into(profilePicture);
                    Uri imageUri = saveImage(bytes);
                    if(imageUri != null) {
                        sharedPreferences.edit().putString(uriImg, imageUri.toString()).apply();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });*/

            /*ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //Erfolg
                    //Es wird nur der Download Link des Bildes erhalten!!!!
                    Glide.with(getContext()).load(uri).into(profilePicture);

                    Glide.with(getContext()).asBitmap().load(uri).into(new Target<Bitmap>() {
                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) { Log.d(TAG, "Glide: onLoadStarted()"); }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) { Log.d(TAG, "Glide: onLoadFailed()"); }

                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Log.d(TAG, "Glide: onResourceReady()");

                            String imagePath = saveImage(resource);
                            sharedPreferences.edit().putString(uriImg, imagePath);
                            Toast.makeText(getContext(),"asdkasdj", Toast.LENGTH_LONG);
                            profilePicture.setImageURI(Uri.parse(imagePath));
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            Log.d(TAG, "Glide: onLoadCleared()");
                        }

                        @Override
                        public void getSize(@NonNull SizeReadyCallback cb) {
                            Log.d(TAG, "Glide: getSize()");
                        }

                        @Override
                        public void removeCallback(@NonNull SizeReadyCallback cb) {
                            Log.d(TAG, "Glide: removeCallback()");
                        }

                        @Override
                        public void setRequest(@Nullable Request request) {
                            Log.d(TAG, "Glide: setRequest()");
                        }

                        @Nullable
                        @Override
                        public Request getRequest() {
                            return null;
                        }

                        @Override
                        public void onStart() {
                            Log.d(TAG, "Glide: onStart()");
                        }

                        @Override
                        public void onStop() {
                            Log.d(TAG, "Glide: onStop()");
                        }

                        @Override
                        public void onDestroy() {
                            Log.d(TAG, "Glide: onDestroy()");
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Error
                    e.printStackTrace();
                }
            });*/

        }

    }

    /**
     * Speichert das heruntergeladene Bild ab.
     * @param bytes sind die heruntergerladenen bytes des Bildes.
     * @return Uri des gepeicherten Bildes.
     */
    private Uri saveImage (byte[] bytes) {
        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        File photo=new File(Environment.getExternalStorageDirectory(), "photo.jpg");

        if(photo.exists()){
            photo.delete();
        }

        try{
            FileOutputStream fos = new FileOutputStream(photo.getAbsolutePath());

            fos.write(bytes);
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        return Uri.parse(photo.getAbsolutePath());

        /*
        String savedImagePath = null;

        String imageFileName = "JPEG_" + userID + ".jpg";

        //Zweites Argument ist der Name unseres Ordners
        File storageDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "Peer Profilbilder");
        boolean success = true;

        //Wenn das Directory noch nicht existiert...
        if(!storageDirectory.exists()){
            // File.mkdir() returns a boolean wether a directory is created or not.(true = created, false = not created)
            success = storageDirectory.mkdir();
        }
        if(success){
            File imageFile = new File(storageDirectory, imageFileName);
            //absolute Path: for example: "C:/ProfilePictures/......"
            savedImagePath = imageFile.getAbsolutePath();

            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                //Arguments: Format, Quality, Outputstream
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (FileNotFoundException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }

            //Add image to system gallery
            addPictureToSystemGallery(savedImagePath);
            Toast.makeText(getContext(), "Image saved", Toast.LENGTH_LONG).show();
        }
        return savedImagePath;
        */
    }

    /**
     * Fügt das Bild zur Galerie App hinzu.
     * @param imagePath path zum Bild.
     */
    private void addPictureToSystemGallery(String imagePath){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        try {
            getActivity().sendBroadcast(mediaScanIntent);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    /**
     * Initialisiert die Variablen, die etwas mit Firebase zu tun haben.
     */
    public void fireBaseAuth() {

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        userID = mAuth.getCurrentUser().getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //Wenn User eingeloggt
                if (user != null) {
                    userID = mAuth.getCurrentUser().getUid();
                } else {

                }
            }
        };

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //retrieving data
                getUserSettings(dataSnapshot);
                setProfileInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * speichert die heruntergeladenen Daten in einem UserSettingsobjekt ab.
     * @param dataSnapshot heruntergeladene Daten
     */
    public void getUserSettings(DataSnapshot dataSnapshot){
        Log.d(TAG, "Retrieving user_settings information from firebase");

        userSettings = new UserSettings();

        for (DataSnapshot ds: dataSnapshot.getChildren()){
            if(ds.getKey().equals("user_settings")){
                Log.d(TAG, "Datasnapshot: " + ds);
                try {
                    userSettings = ds.child(userID).getValue(UserSettings.class);
                }catch(NullPointerException e){
                    Log.d(TAG, "Error occurred loading data: " + e.getMessage());
                }

            }
        }
    }

    /**
     * Setzt den Namen und das Alter in den
     */
    public void setProfileInfo() {
        nameAndAge.setText(userSettings.getUsername() + "(" + userSettings.getAge() + ")");
        description.setText(userSettings.getDescription());
    }

    /*
    //Initialisiert den Image Loader
    public void initializeImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }*/

    /**
     * Checks if the user is connected to the internet. -> This method cannot be modularised because if it is static we cannot reference to the non-static method
     * "getActivity()" which is essential for this method to work.
     * @return wether the user is connected or not.
     */
    private boolean checkInternetConnection(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            //We are connected to a network
            connected = true;
        }
        return connected;
    }


}
