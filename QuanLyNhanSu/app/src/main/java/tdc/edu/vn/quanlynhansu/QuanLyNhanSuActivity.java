package tdc.edu.vn.quanlynhansu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.StringTokenizer;

import tdc.edu.vn.quanlynhansu.adapters.MyListViewAdapter;
import tdc.edu.vn.quanlynhansu.data_models.Person;
import tdc.edu.vn.quanlynhansu.database_layer.PersonDatabase;

public class QuanLyNhanSuActivity extends AppCompatActivity {
    public static String DAIHOC = "University";
    public static String CAODANG = "College";
    public static String TRUNGCAP = "Training";
    public static String KHONGBANGCAP = "None";
    private EditText edtName, edtHoppies;
    private RadioButton radUniversity, radCollege, radTraing;
    private CheckBox chkRead, chkTravel;
    private RadioGroup radioGroup;

    private ArrayList<Person> listMembers = new ArrayList<Person>();
    //private ArrayAdapter<Person> adapter;
    private MyListViewAdapter adapter;
    private PersonDatabase dao;
    private int selectedRow = -1;
    private int backColor;
    private LinearLayout previousItemGround;
    private Button btnAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qlns_contrains_layout);
        // Initialization
        dao = new PersonDatabase(this);
        dao.getPersons(listMembers);

        //Get views from layout
        edtName = (EditText) findViewById(R.id.edtName);
        radUniversity = findViewById(R.id.radUniversity);
        radCollege = findViewById(R.id.radCollege);
        radTraing = findViewById(R.id.radTraining);

        radioGroup = (RadioGroup) findViewById(R.id.radGroup);
        chkRead = (CheckBox) findViewById(R.id.chkRead);
        chkTravel = (CheckBox) findViewById(R.id.chkTravel);
        edtHoppies = (EditText) findViewById(R.id.edtHobbies);
        ListView listView = (ListView) findViewById(R.id.listPerson);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        //Events processing
        //adapter = new ArrayAdapter<Person>(this, android.R.layout.simple_list_item_1, listMembers);

        adapter = new MyListViewAdapter(this, R.layout.listview_item_layout, listMembers);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedRow == -1) {
                    selectedRow = position;
                    disableAdd(btnAdd);
                    setPersonToLayout(listMembers.get(position));
                    LinearLayout bgrItem = view.findViewById(R.id.bgrListItem);
                    // Save the original color of back ground
                    backColor = bgrItem.getSolidColor();
                    bgrItem.setBackgroundColor(getResources().getColor(R.color.selectedRow, getTheme()));
                    previousItemGround = bgrItem;
                }
                else {
                    if (selectedRow == position) {
                        selectedRow = -1;
                        clear();
                        enableAdd(btnAdd);
                        previousItemGround.setBackgroundColor(backColor);
                    } else {
                        selectedRow = position;
                        clear();
                        setPersonToLayout(listMembers.get(position));
                        previousItemGround.setBackgroundColor(backColor);

                        LinearLayout bgrItem = view.findViewById(R.id.bgrListItem);
                        backColor = bgrItem.getSolidColor();
                        bgrItem.setBackgroundColor(getResources().getColor(R.color.selectedRow, getTheme()));
                        previousItemGround = bgrItem;
                    }
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Person nhanSu = createPersonFromLayout();
                if (nhanSu != null) {
                    listMembers.add(nhanSu);
                    dao.savePerson(nhanSu);
                    // Bao cho adapter biet du lieu thay doi
                    adapter.notifyDataSetChanged();
                    clear();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Show the option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Option Menu Processing
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuSave:
                save();
                break;
            case R.id.mnuDelete:
                delete();
                break;
            case R.id.mnuUpdate:
                update();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        dao.savePersons(listMembers);
        //Toast.makeText(this, "The pesons are saved to database!", Toast.LENGTH_LONG).show();
    }

    private void update() {
        if (selectedRow != -1) {
            Person op = listMembers.get(selectedRow);
            Person np = createPersonFromLayout();
            //Up date lại database
            dao.updatePerson(op, np);
            //Update lại list view trên layout
            listMembers.set(selectedRow, np);

            adapter.notifyDataSetChanged();//Nó tạo lại cái listview nên mất hết background nên không cần previousItemGround.setBackgroundColor(backColor);

//            previousItemGround.setBackgroundColor(backColor);

            //Update to the database


            selectedRow = -1;
            enableAdd(btnAdd);
            clear();
        } else {
            showDialog("Update...", "You must select one row before update!", "Hide Update");
        }
    }

    private void delete() {
        if (selectedRow != -1) {
            dao.deletePerson(listMembers.get(selectedRow));
            listMembers.remove(selectedRow);
            adapter.notifyDataSetChanged();
            selectedRow = -1;
            enableAdd(btnAdd);
            clear();
        } else {
            showDialog("Delete...", "You must select one row before delete!", "Hide Delete");
        }
    }

    private void showDialog(String title, String message, String btnName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(btnName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private void enableAdd(Button add) {
        add.setEnabled(true);
    }

    private void disableAdd(Button add) {
        add.setEnabled(false);
    }

    private void setPersonToLayout(Person person) {
        clear();
        edtName.setText(person.getName());
        if (person.getDegree().equalsIgnoreCase(DAIHOC)) {
            radUniversity.setChecked(true);
        } else if (person.getDegree().equalsIgnoreCase(CAODANG)) {
            radCollege.setChecked(true);
        } else if (person.getDegree().equalsIgnoreCase(TRUNGCAP)) {
            radTraing.setChecked(true);
        }

        StringTokenizer tokenizer = new StringTokenizer(person.getHobbies());
        String other = "";
        if (person.getHobbies().contains(chkRead.getText().toString())) {
            chkRead.setChecked(true);
            tokenizer.nextToken(";");
        }
        if (person.getHobbies().contains(chkTravel.getText().toString())) {
            chkTravel.setChecked(true);
            tokenizer.nextToken(";");
        }
        if (tokenizer.hasMoreTokens()) {
            other = tokenizer.nextToken(";");
            edtHoppies.setText(other);
        }

    }

    private Person createPersonFromLayout() {
        if (!edtName.getText().toString().isEmpty()) {
            Person nhanSu = new Person();
            nhanSu.setName(edtName.getText().toString());
            int id = radioGroup.getCheckedRadioButtonId();
            switch (id) {
                case R.id.radTraining:
                    nhanSu.setDegree(TRUNGCAP);
                    break;
                case R.id.radCollege:
                    nhanSu.setDegree(CAODANG);
                    break;
                case R.id.radUniversity:
                    nhanSu.setDegree(DAIHOC);
                    break;
                default:
                    nhanSu.setDegree(KHONGBANGCAP);
                    break;
            }
            String hoppies = "";
            if (chkRead.isChecked()) {
                hoppies += chkRead.getText() + ";";
            }
            if (chkTravel.isChecked()) {
                hoppies += chkTravel.getText() + ";";
            }
            hoppies += edtHoppies.getText();
            nhanSu.setHobbies(hoppies);
            return nhanSu;
        }
        return null;
    }

    private void clear() {
        edtName.setText("");
        radioGroup.clearCheck();
        chkRead.setChecked(false);
        chkTravel.setChecked(false);
        edtHoppies.setText("");
        edtName.requestFocus();
    }
}
