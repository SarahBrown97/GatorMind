package com.example.onecare.questionnaire;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.onecare.R;

import java.util.List;

public class QuestionAdapter extends BaseAdapter {

    Context context;
    List<QuestionClass> data;
    boolean isCompleted;
    private static LayoutInflater inflater = null;

    public QuestionAdapter(Context context, List<QuestionClass> data, boolean isCompleted){
        this.context=context;
        this.data=data;
        this.isCompleted= isCompleted;
        this.inflater=  (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        QuestionClass question = data.get(position);

        if (vi == null) {
            switch (question.type) {
                case TEXT:
                    vi = inflater.inflate(R.layout.question_text_row, null);
                    break;
                case RADIO:
                    vi = inflater.inflate(R.layout.question_radio_row, null);
                    break;
                case RATING:
                    vi = inflater.inflate(R.layout.question_rating_row, null);
                    break;

            }
        }

        TextView text = (TextView) vi.findViewById(R.id.textView);
        text.setText(question.question);
            switch (question.type) {
                case TEXT:
                    EditText editText= (EditText) vi.findViewById(R.id.editTextTextMultiLine);
                    if(isCompleted){
                        editText.setEnabled(false);
                        if(question.answer!=null && !question.answer.isEmpty()){
                            editText.setText(question.answer);
                        }
                    }
                    else{
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                question.answer= editText.getText().toString();
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }

                        });
                    }
                    break;
                case RADIO:
                    RadioGroup radioGroup=(RadioGroup) vi.findViewById(R.id.radioGroup);
                    if(isCompleted){
                        for(int i=0; i<radioGroup.getChildCount();i++){
                            ((RadioButton) radioGroup.getChildAt(i)).setEnabled(false);
                        }
                        if(question.answer!=null && !question.answer.isEmpty()){
                            if(question.answer.equalsIgnoreCase("Yes")){
                                radioGroup.check(R.id.radioButton2);
                            }else{
                                radioGroup.check(R.id.radioButton);
                            }
                        }
                    }
                    else{
                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                if(checkedId==R.id.radioButton2){
                                    question.answer="Yes";
                                } else{
                                    question.answer="No";
                                }
                            }
                        });

                    }
                    break;
                case RATING:
                    SeekBar seekBar=(SeekBar) vi.findViewById(R.id.seekBar);
                    if(isCompleted){
                        if(question.answer!=null&& !question.answer.isEmpty()){
                            try {
                                seekBar.setProgress(Integer.parseInt(question.answer));
                            } catch (Exception exception){

                            }
                        }
                        seekBar.setEnabled(false);
                    }else{
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                question.answer=String.valueOf(progress);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });

                    }
                    break;
            }
            return vi;
        }
}
