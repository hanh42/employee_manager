package tdc.edu.vn.quanlynhansu.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import tdc.edu.vn.quanlynhansu.QuanLyNhanSuActivity;
import tdc.edu.vn.quanlynhansu.R;
import tdc.edu.vn.quanlynhansu.data_models.Person;

public class MyListViewAdapter extends ArrayAdapter<Person> {
    private Activity activity;
    private int layoutID;
    private ArrayList<Person> personList;

    public MyListViewAdapter(Activity activity, int layoutID, ArrayList<Person> personList) {
        super(activity, layoutID, personList);
        this.activity = activity;
        this.layoutID = layoutID;
        this.personList = personList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        view = activity.getLayoutInflater().inflate(layoutID, parent, false);
        Person person = personList.get(position);

        ImageView imgDegree = view.findViewById(R.id.imgDegree);
        TextView lblName = view.findViewById(R.id.lblName);
        TextView lblHobbies = view.findViewById(R.id.lblHobbies);

        if (person.getDegree().equalsIgnoreCase(QuanLyNhanSuActivity.DAIHOC)) {
            imgDegree.setImageDrawable(activity.getResources().getDrawable(R.mipmap.university, activity.getTheme()));
        }
        else if (person.getDegree().equalsIgnoreCase(QuanLyNhanSuActivity.CAODANG)) {
            imgDegree.setImageDrawable(activity.getResources().getDrawable(R.mipmap.college, activity.getTheme()));
        }
        else if (person.getDegree().equalsIgnoreCase(QuanLyNhanSuActivity.TRUNGCAP)) {
            imgDegree.setImageDrawable(activity.getResources().getDrawable(R.mipmap.training, activity.getTheme()));
        }
        else {
            imgDegree.setImageDrawable(activity.getResources().getDrawable(R.mipmap.none, activity.getTheme()));
        }

        lblName.setText(person.getName());
        lblHobbies.setText(person.getHobbies());

        return view;
    }
}
