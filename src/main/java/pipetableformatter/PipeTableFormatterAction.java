package pipetableformatter;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;

public class PipeTableFormatterAction extends EditorAction {

    public PipeTableFormatterAction() {
        super(new FormatActionHandler());
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        if (ActionPlaces.isPopupPlace(e.getPlace())) {
            e.getPresentation().setVisible(e.getPresentation().isEnabled());
        }
    }

    private static class FormatActionHandler extends EditorActionHandler {

        PipeTableFormatter pipeTableFormatter = new PipeTableFormatter();
        TableDetector tableDetector = new TableDetector();

        @Override
        public void execute(final Editor editor, final DataContext dataContext) {
            ApplicationManager.getApplication().runWriteAction(
                    new Runnable() {
                        @Override
                        public void run() {

                            SelectionModel selectionModel = editor.getSelectionModel();

                            if (!selectionModel.hasSelection()) {
                                autoselectTable(selectionModel);
                            }

                            final String text = selectionModel.getSelectedText();

                            if (text != null) {
                                String formattedText = pipeTableFormatter.format(text);
                                editor.getDocument().replaceString(
                                        selectionModel.getSelectionStart(),
                                        selectionModel.getSelectionEnd(),
                                        formattedText
                                );
                            }
                        }

                        private void autoselectTable(SelectionModel selectionModel) {
                            int currentPosition = editor.getCaretModel().getOffset();
                            String text = editor.getDocument().getText();
                            Range tableRange = tableDetector.find(text, currentPosition);

                            if (Range.EMPTY != tableRange) {
                                selectionModel.setSelection(tableRange.getStart(), tableRange.getEnd());
                            }
                        }

                    });
        }
    }
}
