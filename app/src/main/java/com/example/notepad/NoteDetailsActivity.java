package com.example.notepad;

import static io.grpc.InternalChannelz.id;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import org.w3c.dom.Document;

public class NoteDetailsActivity extends AppCompatActivity {
    EditText titleEditText, contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    TextView deleteNoteTextViewBtn;
    String title,content,docID;
    boolean isEditMode=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        titleEditText=findViewById(R.id.notes_title_text);
        contentEditText=findViewById(R.id.notes_content_text);
        saveNoteBtn=findViewById(R.id.save_note_btn);
        pageTitleTextView=findViewById(R.id.page_title);
        deleteNoteTextViewBtn=findViewById(R.id.delete_note_btn);

        title= getIntent().getStringExtra("title");
        content= getIntent().getStringExtra("content");
        docID= getIntent().getStringExtra("docID");

        if(docID!=null && !docID.isEmpty()){
            isEditMode=true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);
        if(isEditMode){
            pageTitleTextView.setText("Edit Your Note");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }

        saveNoteBtn.setOnClickListener((v)->saveNote());
        deleteNoteTextViewBtn.setOnClickListener((v)->deletNoteFromFirebase());
    }

    void saveNote() {
        String noteTitle = titleEditText.getText().toString();
        String notecontent = contentEditText.getText().toString();
        if (noteTitle == null || noteTitle.isEmpty()) {
            titleEditText.setError("Title cannot be empty");
            return;
        }
        Note note=new Note();
        note.setTitle(noteTitle);
        note.setContent(notecontent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);

    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;

        if(isEditMode){
            documentReference=Utility.getCollectionReferenceForNotes().document(docID);
        }else{
            documentReference=Utility.getCollectionReferenceForNotes().document();
        }
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(NoteDetailsActivity.this,"Note added");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this,"Failed to add note");
                }
            }
        });
    }
    void deletNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference=Utility.getCollectionReferenceForNotes().document(docID);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(NoteDetailsActivity.this,"Note deleted");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this,"Failed to delete note");
                }
            }
        });
    }

}