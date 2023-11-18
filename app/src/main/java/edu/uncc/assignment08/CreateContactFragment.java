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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uncc.assignment08.databinding.FragmentCreateContactBinding;
import edu.uncc.assignment08.databinding.PhoneListItemBinding;


public class CreateContactFragment extends Fragment {


    public CreateContactFragment() {
        // Required empty public constructor
    }

    ArrayList<PhoneNumber> mPhoneNumbers = new ArrayList<>();

    public void addPhoneNumbers(PhoneNumber phoneNumber){
        mPhoneNumbers.add(phoneNumber);
    }

    FragmentCreateContactBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateContactBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    PhoneAdapter phoneAdapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phoneAdapter = new PhoneAdapter();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(phoneAdapter);
        binding.imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addPhoneNumber();
            }
        });

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
                    addPhoneNumbersToDB(name, email);
                    mListener.backToContactsFragment();
                }
            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.backToContactsFragment();
            }
        });
    }

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private void addPhoneNumbersToDB(String name, String email){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Contacts").document();

        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("name", name);
        data.put("contact_id", documentReference.getId());
        data.put("created_at", FieldValue.serverTimestamp());
        data.put("created_by_uid", mAuth.getCurrentUser().getUid());
        data.put("phone_numbers", mPhoneNumbers);

        Log.d("Demo", "addPhoneNumbersToDB: " + data);
        documentReference.set(data).
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                }else{
                    Toast.makeText(getActivity(),
                            task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

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
                        notifyDataSetChanged();
                    }
                });
            }

        }
    }
    CreateContactListener mListener;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CreateContactListener) context;
    }

    interface CreateContactListener {
        void addPhoneNumber();
        void backToContactsFragment();
    }
}