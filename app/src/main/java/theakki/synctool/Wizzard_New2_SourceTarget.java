package theakki.synctool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import theakki.synctool.Data.StringTree;
import theakki.synctool.Helper.ViewHelper;
import theakki.synctool.Job.ConnectionTypes.ConnectionTypes;
import theakki.synctool.Job.ConnectionTypes.FTPConnection;
import theakki.synctool.Job.ConnectionTypes.LocalPath;
import theakki.synctool.Job.ConnectionTypes.OwnCloud;
import theakki.synctool.Job.IConnection;
import theakki.synctool.Job.JobHandler;
import theakki.synctool.Job.NamedConnectionHandler;
import theakki.synctool.Job.Settings.OneWayStrategy;
import theakki.synctool.Job.Settings.SyncDirection;
import theakki.synctool.Job.Settings.TwoWayStrategy;
import theakki.synctool.Job.SyncJob;

import static junit.framework.Assert.*;

/**
 * Class to show the second wizzard page for new job
 * @author theakki
 * @since 0.1
 */
public class Wizzard_New2_SourceTarget extends AppCompatActivity
{
    private SyncJob _job;

    private Button _buttonBack;
    private Button _buttonNext;

    private Spinner _spinnerTypeA;
    private Spinner _spinnerTypeB;
    private Spinner _spinnerNameA;
    private Spinner _spinnerNameB;
    private Button _buttonAddA;
    private Button _buttonAddB;
    private EditText _txtRelativePathA;
    private EditText _txtRelativePathB;
    private Button _buttonBrowseA;
    private Button _buttonBrowseB;

    private Spinner _spinnerDirection;
    private Spinner _spinnerStrategy1;
    private Spinner _spinnerStrategy2;

    private String[] _TypesStrings;

    private String _strOldJobName = "";

    private static final int REQUESTCODE_NewOwnCloud = 1;
    private static final int REQUESTCODE_NewFtp = 2;

    private static final int REQUESTCODE_BrowseA = 10;
    private static final int REQUESTCODE_BrowseB = 11;

    public static final int REQUESTCODE_NEXT_PAGE = 101;

    public final static String Extra_ConnectionName = "ConnectionName";

    // ToDo: Test against array
    class SpinnerIdxType
    {
        public final static int Local = 0;
        public final static int Owncloud = 1;
        public final static int Ftp = 2;
    }


    // ToDo: Test against array
    class SpinnerIdxDirection
    {
        public final static int A2B = 0;
        public final static int B2A = 1;
        public final static int A2BB2A = 2;
    }


    // ToDo: Test against array
    private class SpinnerIdxS1W
    {
        public final static int CopyNew = 0;
        public final static int Mirror = 1;
        public final static int NewDate = 2;
        public final static int AllDate = 3;
        public final static int Move = 4;
        public final static int MoveDate = 5;
    }


    // ToDo: Test against array
    private class SpinnerIdxS2W
    {
        public final static int AWins = 0;
        public final static int BWins = 1;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_new2);

        String strSettings = JobHandler.EMPTY_JOB;
        _strOldJobName = "";

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            strSettings = extras.getString(Wizzard_New1_General.SETTINGS);
            _strOldJobName = extras.getString(Wizzard_New1_General.OLD_JOBNAME, "");
        }
        _job = JobHandler.getJob(strSettings);


        // Type Strings
        _TypesStrings = getResources().getStringArray(R.array.ConnectionTypes);
        assertNotNull("Array 'ConnectionTypes' not found");


        // Spinner Type A
        _spinnerTypeA = findViewById(R.id.spn_ConnectionTypeA);
        assertNotNull("Spinner 'Type A' not found", _spinnerTypeA);
        _spinnerTypeA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onTypeSelect(_spinnerTypeA, _spinnerNameA, _buttonAddA);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // Spinner Name A
        _spinnerNameA = findViewById(R.id.spn_ConnectionNameA);
        assertNotNull("Spinner 'Name A' not found", _spinnerNameA);

        // Button Add A
        _buttonAddA = findViewById(R.id.btn_AddConnectionNameA);
        assertNotNull("Button 'Add A' not found", _buttonAddA);

        // Relative Path A
        _txtRelativePathA = findViewById(R.id.txt_LocalPathA);
        assertNotNull("Text 'Relative Path A' not found", _txtRelativePathA);

        // Browse A
        _buttonBrowseA = findViewById(R.id.btn_BrowseA);
        assertNotNull("Button 'Browse A' not found", _buttonBrowseA);
        _buttonBrowseA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browse(_spinnerTypeA, _spinnerNameA, REQUESTCODE_BrowseA);
            }
        });

        // Spinner Type B
        _spinnerTypeB = findViewById(R.id.spn_ConnectionTypeB);
        assertNotNull("Spinner 'Type B' not found", _spinnerTypeB);
        _spinnerTypeB.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onTypeSelect(_spinnerTypeB, _spinnerNameB, _buttonAddB);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {}
        });

        // Spinner Name B
        _spinnerNameB = findViewById(R.id.spn_ConnectionNameB);
        assertNotNull("Spinner 'Name A' not found", _spinnerNameB);

        // Button Add B
        _buttonAddB = findViewById(R.id.btn_AddConnectionNameB);
        assertNotNull("Button 'Add A' not found", _buttonAddB);

        // Relative Path B
        _txtRelativePathB = findViewById(R.id.txt_LocalPathB);
        assertNotNull("Text 'Relative Path B' not found", _txtRelativePathB);

        // Browse B
        _buttonBrowseB = findViewById(R.id.btn_BrowseB);
        assertNotNull("Button 'Browse B' not found", _buttonBrowseB);
        _buttonBrowseB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browse(_spinnerTypeB, _spinnerNameB, REQUESTCODE_BrowseB);
            }
        });

        // Spinner Sync Direction
        _spinnerDirection = findViewById(R.id.spn_SyncDirection);
        assertNotNull("Spinner 'Direction' not found", _spinnerDirection);
        _spinnerDirection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onDirectionSelect();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {}
        });

        // Spinner Sync Strategy One Way
        _spinnerStrategy1 = findViewById(R.id.spn_StrategyOneWay);
        assertNotNull("Spinner 'Strategy 1' not found", _spinnerStrategy1);

        // Spinner Sync Strategy Two Way
        _spinnerStrategy2 = findViewById(R.id.spn_StrategyTwoWay);
        assertNotNull("Spinner 'Strategy 2' not found", _spinnerStrategy2);


        // Back
        _buttonBack = findViewById(R.id.btn_Back);
        assertNotNull("Button 'Back' not found", _buttonBack);
        _buttonBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickBack();
            }
        });

        // Next
        _buttonNext = findViewById(R.id.btn_Next);
        assertNotNull("Button 'Next' not found", _buttonNext);
        _buttonNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onNextClick();
            }
        });

        restoreDataFromJob();
    }

    private void clickBack()
    {
        Intent intentBack = new Intent();
        intentBack.putExtra(Wizzard_New1_General.SETTINGS, JobHandler.getSettings(_job) );
        setResult(Activity.RESULT_CANCELED, intentBack);
        finish();
    }

    private void browse(Spinner spnType, Spinner spnName, int Requestcode)
    {
        final int iSelectedType = spnType.getSelectedItemPosition();
        final String strSelectedName = (String)spnName.getSelectedItem();
        final Intent intentBrowse = new Intent(Wizzard_New2_SourceTarget.this, Wizzard_FolderBrowser.class);
        final IConnection connection;
        final int iRequestcode = Requestcode;

        switch(iSelectedType)
        {
            case SpinnerIdxType.Local: // Local
                final String BasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                connection = new LocalPath(BasePath);
                intentBrowse.putExtra(Wizzard_FolderBrowser.EXTRA_RECEIVE_PATH_OFFSET, BasePath);
                break;

            case SpinnerIdxType.Owncloud:
                connection = new OwnCloud(strSelectedName, "/");
                break;

            case SpinnerIdxType.Ftp:    // FTP
                connection = new FTPConnection(strSelectedName, "/");
                break;

            default:
                return;
        }

        // ProgressDialog to show
        final ProgressDialog progress = ProgressDialog.show(Wizzard_New2_SourceTarget.this, getString(R.string.Status_PleaseWait), getString(R.string.Status_CreateFolderTree), true, false);

        // Thread to avoid blocking ui thread
        new Thread() {
            @Override
            public void run()
            {
                readTree(connection);
                progress.dismiss();
                intentBrowse.putExtra(Wizzard_FolderBrowser.EXTRA_RECEIVE_FOLDERS, _Tree);
                startActivityForResult(intentBrowse, iRequestcode);
            }
        }.start();
    }


    private StringTree _Tree;
    private void readTree(IConnection connection)
    {
        connection.RequestPermissions(this);
        connection.Connect(this);
        _Tree = connection.Tree();
    }


    @Override
    protected void onResume() {
        super.onResume();

        //restoreDataFromJob();
    }


    private void restoreDataFromJob()
    {
        IConnection iConnectionA = _job.SideA();
        IConnection iConnectionB = _job.SideB();

        restoreConnection(iConnectionA, _spinnerTypeA, _spinnerNameA, _txtRelativePathA);
        restoreConnection(iConnectionB, _spinnerTypeB, _spinnerNameB, _txtRelativePathB);

        restoreDirection();
        onDirectionSelect();
        restoreStrategyOneWay();
        restoreStrategyTwoWay();
    }


    private void restoreStrategyOneWay()
    {
        final OneWayStrategy strategy = _job.StrategyOneWay();
        switch(strategy)
        {
            case Mirror:
                _spinnerStrategy1.setSelection(SpinnerIdxS1W.Mirror);
                break;
            case Standard:
                _spinnerStrategy1.setSelection(SpinnerIdxS1W.CopyNew);
                break;
            case AllFilesInDateFolder:
                _spinnerStrategy1.setSelection(SpinnerIdxS1W.AllDate);
                break;
            case NewFilesInDateFolder:
                _spinnerStrategy1.setSelection(SpinnerIdxS1W.NewDate);
                break;
        }
    }


    private void restoreStrategyTwoWay()
    {
        final TwoWayStrategy strategy = _job.StrategyTwoWay();
        switch(strategy)
        {
            case AWins:
                _spinnerStrategy2.setSelection(SpinnerIdxS2W.AWins);
                break;
            case BWins:
                _spinnerStrategy2.setSelection(SpinnerIdxS2W.BWins);
                break;
        }
    }


    private void restoreDirection()
    {
        final SyncDirection direction = _job.Direction();
        switch(direction)
        {
            case Booth:
                _spinnerDirection.setSelection(SpinnerIdxDirection.A2BB2A);
                break;
            case ToA:
                _spinnerDirection.setSelection(SpinnerIdxDirection.B2A);
                break;
            case ToB:
                _spinnerDirection.setSelection(SpinnerIdxDirection.A2B);
                break;
        }
    }


    private void restoreRelativePath(EditText txt, String strRelPath)
    {
        txt.setText(strRelPath);
    }


    private void restoreConnection(IConnection conn, Spinner spnType, Spinner spnName, EditText txtPath)
    {
        if(conn != null)
        {
            String strType = conn.Type();
            if(strType.equals(OwnCloud.getType()))
            {
                OwnCloud owncloud = (OwnCloud)conn;

                spnType.setSelection(SpinnerIdxType.Owncloud);
                //onTypeSelect(spnType, spnName, _buttonAddA); // anzeigen der benötigten Views
                fillSpinnerWithOwnCloudConnections(spnName);

                ViewHelper.selectSpinnerElementByString(spnName, owncloud.ConnectionName());
                restoreRelativePath(txtPath, owncloud.LocalPath());
            }
            else if(strType.equals(LocalPath.getType()))
            {
                LocalPath local = (LocalPath)conn;

                spnType.setSelection(SpinnerIdxType.Local);
                restoreRelativePath(txtPath, local.Path());
            }
            else if(strType.equals(FTPConnection.getType()))
            {
                spnType.setSelection(SpinnerIdxType.Ftp);
            }
        }
    }


    private void fillData(Spinner spnType, Spinner spnName)
    {
        final int iSelected = spnType.getSelectedItemPosition();

        switch(iSelected)
        {
            case SpinnerIdxType.Local: // Local
                break;

            case SpinnerIdxType.Owncloud: // OwnCloud
                // Fill with all available connections
                fillSpinnerWithOwnCloudConnections(spnName);
                if(spnName.getAdapter().getCount() == 1)
                    spnName.setSelection(0);
                break;

            case SpinnerIdxType.Ftp:    // FTP
                fillSpinnerWithFtpConnections(spnName);
                if(spnName.getAdapter().getCount() == 1)
                    spnName.setSelection(0);
                break;
        }
    }


    private void onTypeSelect(Spinner spnType, Spinner spnName, Button btnAdd)
    {
        final int iSelected = spnType.getSelectedItemPosition();

        switch(iSelected)
        {
            case SpinnerIdxType.Local: // Local
                spnName.setVisibility(View.GONE);
                btnAdd.setVisibility(View.GONE);
                break;

            case SpinnerIdxType.Owncloud: // OwnCloud
                spnName.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        callNewOwncloudConnection();
                    }
                });

                // Fill with all available connections
                fillSpinnerWithOwnCloudConnections(spnName);

                // When no connection available -> Start Dialog
                if(spnName.getAdapter().getCount() == 0)
                    callNewOwncloudConnection();
                break;

            case SpinnerIdxType.Ftp: // FTP
                spnName.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        callNewFtpConnection();
                    }
                });

                fillSpinnerWithFtpConnections(spnName);
                if(spnName.getAdapter().getCount() == 0)
                    callNewFtpConnection();
                break;
        }
    }


    private void onDirectionSelect()
    {
        final int iSelected = _spinnerDirection.getSelectedItemPosition();
        switch(iSelected)
        {
            case SpinnerIdxDirection.A2B: // fall through
            case SpinnerIdxDirection.B2A:
                _spinnerStrategy1.setVisibility(View.VISIBLE);
                _spinnerStrategy2.setVisibility(View.GONE);
                break;

            case SpinnerIdxDirection.A2BB2A:
                _spinnerStrategy1.setVisibility(View.GONE);
                _spinnerStrategy2.setVisibility(View.VISIBLE);
        }
    }


    private void callNewOwncloudConnection()
    {
        Intent intentNew = new Intent(Wizzard_New2_SourceTarget.this, Wizzard_NewOwnCloudConnection.class);
        startActivityForResult(intentNew, REQUESTCODE_NewOwnCloud);
    }


    private void callNewFtpConnection()
    {
        Intent intentNew = new Intent(Wizzard_New2_SourceTarget.this, Wizzard_NewFtpConnection.class);
        startActivityForResult(intentNew, REQUESTCODE_NewFtp);
    }


    private void fillSpinnerWithOwnCloudConnections(Spinner spnSpinner)
    {
        List<String> list = new ArrayList<>();

        // fill here
        ArrayList<NamedConnectionHandler.Connections> connections =
            NamedConnectionHandler.getInstance().getConnections(ConnectionTypes.OwnCloud);
        for(NamedConnectionHandler.Connections con : connections)
        {
            list.add(con.Name);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSpinner.setAdapter(dataAdapter);
    }


    private void fillSpinnerWithFtpConnections(Spinner spnSpinner)
    {
        List<String> list = new ArrayList<>();

        // fill here
        ArrayList<NamedConnectionHandler.Connections> connections =
            NamedConnectionHandler.getInstance().getConnections(ConnectionTypes.FTP);
        for(NamedConnectionHandler.Connections con : connections)
        {
            list.add(con.Name);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSpinner.setAdapter(dataAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case REQUESTCODE_NEXT_PAGE:
                if(resultCode == RESULT_OK)
                {
                    setResult(RESULT_OK, data);
                    finish();
                }
                else if(resultCode == RESULT_CANCELED)
                {
                    //ToDo: Decide if necessary. Normally no page from wizzard should change any from this page
                    final String strSettings = data.getStringExtra(Wizzard_New1_General.SETTINGS);
                    if(strSettings.length() > 0)
                        _job = JobHandler.getJob(strSettings);
                    restoreDataFromJob();
                }
                break;

            case REQUESTCODE_NewOwnCloud:
                if (resultCode == RESULT_OK) // Activity.RESULT_OK
                {
                    fillData(_spinnerTypeA, _spinnerNameA);
                    fillData(_spinnerTypeB, _spinnerNameB);
                }
                break;

            case REQUESTCODE_NewFtp:
                if(resultCode == RESULT_OK)
                {
                    fillData(_spinnerTypeA, _spinnerNameA);
                    fillData(_spinnerTypeB, _spinnerNameB);
                }
                break;

            case REQUESTCODE_BrowseA:
                if(resultCode == RESULT_OK)
                {
                    final String folder = data.getStringExtra(Wizzard_FolderBrowser.EXTRA_SEND_SELECTED);
                    _txtRelativePathA.setText( folder );
                }
                break;

            case REQUESTCODE_BrowseB:
                if(resultCode == RESULT_OK)
                {
                    final String folder = data.getStringExtra(Wizzard_FolderBrowser.EXTRA_SEND_SELECTED);
                    _txtRelativePathB.setText( folder );
                }
                break;
        }
    }


    private void saveConnections()
    {
        _job.SideA( getConnection(_spinnerTypeA, _spinnerNameA, _txtRelativePathA) );
        _job.SideB( getConnection(_spinnerTypeB, _spinnerNameB, _txtRelativePathB) );
    }


    private void saveDirectionAndStrategy()
    {
        final int iSelectedDirection = _spinnerDirection.getSelectedItemPosition();
        switch(iSelectedDirection)
        {
            case SpinnerIdxDirection.A2B:
                _job.Direction(SyncDirection.ToB);
                break;

            case SpinnerIdxDirection.B2A:
                _job.Direction(SyncDirection.ToA);
                break;

            case SpinnerIdxDirection.A2BB2A:
                _job.Direction(SyncDirection.Booth);
                break;

            default:
                assertTrue("Invalid SpinnerIdx 'Sync Direction: " + iSelectedDirection, false);

        }

        final int iSelectedStrategy1 = _spinnerStrategy1.getSelectedItemPosition();
        switch(iSelectedStrategy1)
        {
            case SpinnerIdxS1W.CopyNew:
                _job.StrategyOneWay(OneWayStrategy.Standard);
                break;

            case SpinnerIdxS1W.Mirror:
                _job.StrategyOneWay(OneWayStrategy.Mirror);
                break;

            case SpinnerIdxS1W.NewDate:
                _job.StrategyOneWay(OneWayStrategy.NewFilesInDateFolder);
                break;

            case SpinnerIdxS1W.AllDate:
                _job.StrategyOneWay(OneWayStrategy.AllFilesInDateFolder);
                break;

            case SpinnerIdxS1W.Move:
                _job.StrategyOneWay(OneWayStrategy.MoveFiles);
                break;

            case SpinnerIdxS1W.MoveDate:
                _job.StrategyOneWay(OneWayStrategy.MoveFilesInDateFolder);
                break;

            default:
                assertTrue("Invalid SpinnerIdx 'Sync One Way': " + iSelectedStrategy1, false);

        }

        final int iSelectedStrategy2 = _spinnerStrategy2.getSelectedItemPosition();
        switch(iSelectedStrategy2)
        {
            case SpinnerIdxS2W.AWins:
                _job.StrategyTwoWay(TwoWayStrategy.AWins);
                break;

            case SpinnerIdxS2W.BWins:
                _job.StrategyTwoWay(TwoWayStrategy.BWins);
                break;

            default:
                assertTrue("Invalid SpinnerIdx 'Sync Two Way': " + iSelectedStrategy2, false);
        }
    }


    @Nullable private IConnection getConnection(Spinner spnType, Spinner spnName, EditText txtPath)
    {
        final int iSelected = spnType.getSelectedItemPosition();

        switch(iSelected)
        {
            case SpinnerIdxType.Local:
                LocalPath l = new LocalPath(txtPath.getText().toString());
                return l;

            case SpinnerIdxType.Owncloud: // OwnCloud
                OwnCloud o = new OwnCloud(spnName.getSelectedItem().toString(), txtPath.getText().toString());
                return o;

            case SpinnerIdxType.Ftp: // FTP
                FTPConnection f = new FTPConnection(spnName.getSelectedItem().toString(), txtPath.getText().toString());
                return f;
        }
        return null;
    }


    private void onNextClick()
    {
        if(_txtRelativePathA.getText().toString().length() == 0 || _txtRelativePathB.getText().toString().length() == 0)
        {
            Toast.makeText(this, R.string.Toast_NoPathSelected, Toast.LENGTH_SHORT).show();
            return;
        }

        saveConnections();
        saveDirectionAndStrategy();

        Intent intentNext = new Intent(Wizzard_New2_SourceTarget.this, Wizzard_New3_Trigger.class);
        final String strSettings = JobHandler.getSettings(_job);
        intentNext.putExtra(Wizzard_New1_General.SETTINGS, strSettings );
        intentNext.putExtra(Wizzard_New1_General.OLD_JOBNAME, _strOldJobName);
        startActivityForResult(intentNext, REQUESTCODE_NEXT_PAGE);
    }
}
