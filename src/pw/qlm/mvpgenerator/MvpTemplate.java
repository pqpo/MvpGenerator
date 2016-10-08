package pw.qlm.mvpgenerator;

/**
 * Created by Administrator on 2016/9/30.
 */
interface MvpTemplate {

    String CONTRACT_TEMPLATE =
            "package %s.contract;\n" +
                    "\n" +
                    "/**\n * Created by MvpGenerator.\n */\n" +
                    "public interface %sContract {\n" +
                    "interface View extends IView {\n" +
                    "}\n" +
                    "\n" +
                    "interface Presenter extends IPresenter<View> {\n" +
                    "}\n" +
                    "\n" +
                    "interface Model extends IModel {\n" +
                    "}\n" +
                    "}";

    String PRESENTER_TEMPLATE = "package %s.presenter;\n" +
            "\n" +
            "/**\n * Created by MvpGenerator.\n */\n" +
            "public class %sPresenter extends BasePresenter<%sContract.View, %sContract.Model> implements %sContract.Presenter {\n" +
            "}";

    String MODEL_TEMPLATE = "package %s.model;\n" +
            "\n" +
            "/**\n * Created by MvpGenerator for CreditCard.\n */\n" +
            "public class %sModel implements %sContract.Model {\n" +
            "}";


}
