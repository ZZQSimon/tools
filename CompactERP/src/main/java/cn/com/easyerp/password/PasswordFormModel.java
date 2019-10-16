package cn.com.easyerp.password;

import cn.com.easyerp.core.view.TableBasedFormModel;
import cn.com.easyerp.core.widget.FieldWithRepresentationModel;
import cn.com.easyerp.framework.enums.ActionType;

public class PasswordFormModel extends TableBasedFormModel {
    private FieldWithRepresentationModel NewPassword;
    private FieldWithRepresentationModel OldPassword;
    private FieldWithRepresentationModel RepeatPassword;

    public PasswordFormModel(ActionType action, String parent, FieldWithRepresentationModel NewPassword,
            FieldWithRepresentationModel OldPassword, FieldWithRepresentationModel RepeatPassword, String table) {
        super(action, parent, table);
        this.NewPassword = NewPassword;
        NewPassword.setParent(getId());

        this.OldPassword = OldPassword;
        OldPassword.setParent(getId());

        this.RepeatPassword = RepeatPassword;
        RepeatPassword.setParent(getId());
    }

    public FieldWithRepresentationModel getNewPassword() {
        return this.NewPassword;
    }

    public FieldWithRepresentationModel getOldPassword() {
        return this.OldPassword;
    }

    public FieldWithRepresentationModel getRepeatPassword() {
        return this.RepeatPassword;
    }

    protected String getView() {
        return "changepassword";
    }

    public String getTitle() {
        return "changepassword";
    }
}
