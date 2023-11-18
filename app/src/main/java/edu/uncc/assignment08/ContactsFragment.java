package edu.uncc.assignment08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import edu.uncc.assignment08.databinding.ContactListItemBinding;
import edu.uncc.assignment08.databinding.FragmentContactsBinding;

public class ContactsFragment extends Fragment {
    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    FragmentContactsBinding binding;
    ArrayList<Contact> mContacts = new ArrayList<>();
    ListenerRegistration listenerRegistration;

    ContactsAdapter contactsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Contacts");

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsAdapter = new ContactsAdapter();
        binding.recyclerView.setAdapter(contactsAdapter);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        listenerRegistration = db.collection("Contacts")
                .whereEqualTo("created_by_uid", FirebaseAuth.getInstance().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.w("demo", "onEvent: ", error);
                    return;
                }
                mContacts.clear();

                for (QueryDocumentSnapshot document : value){
                    Log.d("Demo", "DocumentSnapshot data: " + document.getData());
                    Contact contact = document.toObject(Contact.class);
                    Log.d("Demo", "onComplete :" + contact);

                    mContacts.add(contact);
                }
                contactsAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration!=null){
            listenerRegistration.remove();
        }
    }

    class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>{

        @NonNull
        @Override
        public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ContactListItemBinding binding = ContactListItemBinding.inflate(getLayoutInflater(),
                    parent, false);
            return new ContactsViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
            Contact contactDetails = mContacts.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.contactDetails(contactDetails);
                }
            });
            holder.setupUI(contactDetails);
        }

        @Override
        public int getItemCount() {
            return mContacts.size();
        }

        class ContactsViewHolder extends RecyclerView.ViewHolder{

            ContactListItemBinding mBinding;
            public ContactsViewHolder(@NonNull ContactListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(Contact contact){
                mBinding.textViewEmail.setText(contact.getEmail());
                mBinding.textViewName.setText(contact.getName());
                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("Contacts").document(contact.getContact_id()).delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(getActivity(),
                                                    "contact deleted", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    }
                });

                if (contact.getPhone_numbers().size() > 0) {
                    PhoneNumber phoneNumber = contact.getPhone_numbers().get(0);
                    String message = phoneNumber.getNumber() + " (" + phoneNumber.getType() + ")";
                    if (contact.getPhone_numbers().size() > 1) {
                        int remaining = contact.getPhone_numbers().size() - 1;
                        mBinding.textViewPhoneNumber.setText(message + " and " + remaining + " others");
                    } else {
                        mBinding.textViewPhoneNumber.setText(message);
                    }
                } else {
                    mBinding.textViewPhoneNumber.setText("no contacts");
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_new_item) {
            mListener.createContact();
            return true;
        } else if(item.getItemId() == R.id.logout_item){
            mListener.logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ContactsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ContactsListener) context;
    }

    interface ContactsListener {
        void createContact();
        void logout();

        void contactDetails(Contact contact);
    }
}