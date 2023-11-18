package edu.uncc.assignment08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.uncc.assignment08.databinding.FragmentContactDetailsBinding;
import edu.uncc.assignment08.databinding.PhoneListItemBinding;


public class ContactDetailsFragment extends Fragment {


    private static final String ARG_PARAM_CONTACT = "ARG_PARAM_CONTACT";

    ArrayList<PhoneNumber> mPhoneNumbers = new ArrayList<>();
    private Contact contact;

    public ContactDetailsFragment() {
        // Required empty public constructor
    }

    public static ContactDetailsFragment newInstance(Contact contact) {
        ContactDetailsFragment fragment = new ContactDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CONTACT, contact);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contact = (Contact) getArguments().getSerializable(ARG_PARAM_CONTACT);
        }
        setHasOptionsMenu(true);
    }

    FragmentContactDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    ContactDetailsAdapter contactDetailsAdapter;

    ListenerRegistration contactListenerRegistration;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPhoneNumbers.clear();
        binding.textViewEmail.setText(contact.getEmail());
        binding.textViewName.setText(contact.getName());

        mPhoneNumbers.addAll(contact.getPhone_numbers());
        contactDetailsAdapter = new ContactDetailsAdapter(getContext(), R.layout.phone_list_item, mPhoneNumbers);

        binding.listView.setAdapter(contactDetailsAdapter);

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goBack();
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        contactListenerRegistration = db.collection("Contacts")
                .whereEqualTo("created_by_uid", FirebaseAuth.getInstance().getUid())
                .whereEqualTo("contact_id", contact.getContact_id())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            Log.w("demo", "onEvent: ", error);
                            return;
                        }

                        mPhoneNumbers.clear();

                        for (QueryDocumentSnapshot document : value){
                            Log.d("Demo", "DocumentSnapshot data: " + document.getData());
                            Contact contactDoc = document.toObject(Contact.class);
                            Log.d("Demo", "onComplete :" + contact);
                            binding.textViewEmail.setText(contactDoc.getEmail());
                            binding.textViewName.setText(contactDoc.getName());
                            mPhoneNumbers.addAll(contactDoc.getPhone_numbers());
                            contact.setEmail(contactDoc.getEmail());
                            contact.setName(contactDoc.getName());
                            contact.getPhone_numbers().clear();
                            contact.getPhone_numbers().addAll(mPhoneNumbers);
                        }
                        contactDetailsAdapter.notifyDataSetChanged();
                    }
                });
    }

    class ContactDetailsAdapter extends ArrayAdapter<PhoneNumber> {

        public ContactDetailsAdapter(@NonNull Context context, int resource, @NonNull List<PhoneNumber> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            PhoneListItemBinding phoneListItemBinding;
            if (convertView == null){
                phoneListItemBinding = PhoneListItemBinding.inflate(getLayoutInflater(), parent, false);
                convertView = phoneListItemBinding.getRoot();
                convertView.setTag(phoneListItemBinding);
            }else {
                phoneListItemBinding = (PhoneListItemBinding) convertView.getTag();
            }

            PhoneNumber phoneNumber = mPhoneNumbers.get(position);
            phoneListItemBinding.textViewType.setText(phoneNumber.getType());
            phoneListItemBinding.textViewNumber.setText(phoneNumber.getNumber());
            phoneListItemBinding.imageViewDelete.setVisibility(View.INVISIBLE);

            return convertView;
        }
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.contact_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.edit_item) {
            if (contact!= null) {
                mListener.editContact(contact);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (contactListenerRegistration !=null){
            contactListenerRegistration.remove();
        }
    }

    ContactDetailsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ContactDetailsListener) context;
    }

    interface ContactDetailsListener{
        void goBack();

        void editContact(Contact contact);
    }
}