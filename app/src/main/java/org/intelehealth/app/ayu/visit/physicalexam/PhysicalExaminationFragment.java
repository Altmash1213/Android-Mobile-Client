package org.intelehealth.app.ayu.visit.physicalexam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.intelehealth.app.R;
import org.intelehealth.app.ayu.visit.VisitCreationActionListener;
import org.intelehealth.app.ayu.visit.VisitCreationActivity;
import org.intelehealth.app.ayu.visit.common.adapter.QuestionsListingAdapter;
import org.intelehealth.app.knowledgeEngine.Node;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhysicalExaminationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhysicalExaminationFragment extends Fragment {

    //private List<Node> mCurrentRootOptionList = new ArrayList<>();
    private int mCurrentComplainNodeOptionsIndex = 0;
    private QuestionsListingAdapter mQuestionsListingAdapter;
    private Node mCurrentNode;
    private VisitCreationActionListener mActionListener;

    public PhysicalExaminationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActionListener = (VisitCreationActionListener) context;
        //sessionManager = new SessionManager(context);
    }

    public static PhysicalExaminationFragment newInstance(Intent intent, Node node) {
        PhysicalExaminationFragment fragment = new PhysicalExaminationFragment();
        fragment.mCurrentNode = node;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_physical_examination, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rcv_questions);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        //mCurrentRootOptionList = mCurrentNode.getOptionsList();

        mQuestionsListingAdapter = new QuestionsListingAdapter(recyclerView, getActivity(), mCurrentNode.getOptionsList().size(), new QuestionsListingAdapter.OnItemSelection() {
            @Override
            public void onSelect(Node node) {
                Log.v("onSelect", "node - " + node.getText());
                if (mCurrentComplainNodeOptionsIndex < mCurrentNode.getOptionsList().size() - 1)
                    mCurrentComplainNodeOptionsIndex++;
                else {
                    mCurrentComplainNodeOptionsIndex = 0;

                }
                mQuestionsListingAdapter.addItem(mCurrentNode.getOptionsList().get(mCurrentComplainNodeOptionsIndex));
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                    }
                }, 100);

                mActionListener.onProgress((int) 100 / mCurrentNode.getOptionsList().size());
            }

            @Override
            public void needTitleChange(String title) {
                // mActionListener.onTitleChange(title);
            }

            @Override
            public void onAllAnswered(boolean isAllAnswered) {
                mActionListener.onFormSubmitted(VisitCreationActivity.STEP_2_VISIT_REASON_QUESTION_SUMMARY, null);
            }

            @Override
            public void onCameraRequest() {
                mActionListener.onCameraOpenRequest();
            }
        });

        recyclerView.setAdapter(mQuestionsListingAdapter);
        mQuestionsListingAdapter.addItem(mCurrentNode.getOptionsList().get(mCurrentComplainNodeOptionsIndex));
        return view;
    }
}