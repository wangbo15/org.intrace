package intrace.ecl.ui.output;

import intrace.ecl.ui.output.EditorInput.InputType;

import java.net.Socket;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.intrace.client.gui.helper.InTraceUI;
import org.intrace.client.gui.helper.Connection.ConnectState;
import org.intrace.client.gui.helper.InTraceUI.IConnectionStateCallback;
import org.intrace.client.gui.helper.InTraceUI.UIMode;

public class InTraceEditor extends EditorPart
{      
  private InTraceUI inTraceUI;

  public InTraceEditor()
  {
    // Do nothing
  }

  @Override
  public void createPartControl(final Composite parent)
  {
    IWorkbench workbench = PlatformUI.getWorkbench();
    final Display display = workbench.getDisplay();
    Shell window = display.getActiveShell();
    inTraceUI = new InTraceUI(window, parent, UIMode.ECLIPSE);
    inTraceUI.setConnCallback(new IConnectionStateCallback()
    {     
      @Override
      public void setConnectionState(final ConnectState state)
      {
        if (!parent.isDisposed())
        {
          display.syncExec(new Runnable()
          {            
            @Override
            public void run()
            {
              setPartName("InTrace: " + state.str);
            }
          });
        }
      }
    });
    inTraceUI.setConnectionState(ConnectState.CONNECTING);
    final EditorInput intraceInput = (EditorInput)getEditorInput();
    if (intraceInput.type == InputType.NEWCONNECTION)
    {
      new Thread(new Runnable()
      {      
        @Override
        public void run()
        {
          try
          {          
            Socket connection = intraceInput.callback.getClientConnection();
            inTraceUI.setFixedLocalConnection(connection);
          }
          catch (InterruptedException e)
          {
            e.printStackTrace();
          }       
        }
      }).start();
    }
    else
    {
      inTraceUI.setFixedLocalConnection(intraceInput.callback.agentServerPort);
    }
  }

  @Override
  public void init(IEditorSite site, 
                   IEditorInput input)
      throws PartInitException
  {
    setSite(site);
    setInput(input);          
  }

  @Override
  public void dispose()
  {
    inTraceUI.dispose();
    super.dispose();
  }
  
  @Override
  public void setFocus()
  {
    // Do nothing
  }
  
  @Override
  public void doSave(IProgressMonitor monitor)
  {
    // Do nothing
  }

  @Override
  public void doSaveAs()
  {
    // Do nothing
  }

  @Override
  public boolean isDirty()
  {
    return false;
  }

  @Override
  public boolean isSaveAsAllowed()
  {
    return false;
  }
}