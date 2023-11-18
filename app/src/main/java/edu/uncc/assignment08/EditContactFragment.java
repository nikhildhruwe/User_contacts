package edu.uncc.assignment08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.uncc.assignment08.databinding.FragmentContactDetailsBinding;
import edu.uncc.assignment08.databinding.FragmentEditContactBinding;
import edu.uncc.assignment08.databinding.PhoneListItemBinding;


public class EditContactFragment extends Fragment {

    private static final String ARG_PARAM_CONTACT = "ARG_PARAM_CONTACT";

    private Contact contact;

    public void addPhoneNumber(PhoneNumber phoneNumber){
        contact.getPhone_numbers().add(phoneNumber);
    }
    public EditContactFragment() {
        // Required empty public constructor
    }

    public static EditContactFragment newInstance(Contact contact) {
        EditContactFragment fragment = new EditContactFragment();
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
    }

    FragmentEditContactBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditContactBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPhoneNumbers.clear();
        binding.editTextName.setText(contact.getName());
        binding.editTextEmailAddress.setText(contact.getEmail());

        mPhoneNumbers.addAll(contact.getPhone_numbers());

        adapter = new PhoneAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);


        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.editTextName.getText().toString();
                String email = binding.editTextEmailAddress.getText().toString();

                if (name.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid name!", Toast.LENGTH_SHORT).show();

                } else if (email.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter valid email!", Toast.LENGTH_SHORT).show();

                }else if (mPhoneNumbers.size() < 1){
                    Toast.makeText(getActivity(), "Add at least one contact!", Toast.LENGTH_SHORT).show();
                } else {
                    updateContact(name, email);
                    mListener.goBack();
                }
            }


        });
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goBack();
            }
        });

        binding.imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addPhoneNumber();
            }
        });
    }

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private void updateContact(String name, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentReference = db.collection("Contacts").
                document(contact.getContact_id());

        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("name", name);
        data.put("contact_id", documentReference.getId());
        data.put("created_at", FieldValue.serverTimestamp());
        data.put("created_by_uid", mAuth.getCurrentUser().getUid());
        data.put("phone_numbers", mPhoneNumbers);

        documentReference.update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
//                    Toast.makeText(getActivity(),
//                            "contact updated", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity(),
                            task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    ArrayList<PhoneNumber> mPhoneNumbers = new ArrayList<>();

    PhoneAdapter adapter;

    class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder>{


        @NonNull
        @Override
        public PhoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            PhoneListItemBinding binding = PhoneListItemBinding.inflate(getLayoutInflater(), parent,
                    false);
            return new PhoneViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull PhoneViewHolder holder, int position) {
            holder.setupUI(mPhoneNumbers.get(position));
        }

        @Override
        public int getItemCount() {
            return mPhoneNumbers.size();
        }

        class PhoneViewHolder extends RecyclerView.ViewHolder{
            PhoneListItemBinding mBinding;
            public PhoneViewHolder(@NonNull PhoneListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(PhoneNumber phoneNumber){

                mBinding.textViewNumber.setText(phoneNumber.getNumber());
                mBinding.textViewType.setText(phoneNumber.getType());

                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPhoneNumbers.remove(phoneNumber);
                        contact.getPhone_numbers().remove(phoneNumber);

                        notifyDataSetChanged();
                        deleteNumberFromContact(phoneNumber);
                    }
                });
            }

        }
    }

    private void deleteNumberFromContact(PhoneNumber phoneNumber) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference contactsRef = db.collection("Contacts").document(contact.getContact_id());

        contactsRef.update("phone_numbers", mPhoneNumbers).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getActivity(), "Contact updated", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    EditContactListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (EditContactListener) context;
    }

    interface EditContactListener{
        void addPhoneNumber();
        void goBack();
    }
}