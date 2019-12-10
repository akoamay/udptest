package akoamay.cell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * シンプルなコマンドライン引数解析クラス
 */
public class CommandLineParser {

    private final Map<String, Boolean> candidates_ = new HashMap<String, Boolean>();
    private final Map<String, String> options_ = new HashMap<String, String>();
    private final List<String> args_ = new ArrayList<String>();
    private int requiredArgSize_ = 0;

    /**
     * 省略可能なオプションを追加します。
     *
     * @param option 追加するオプション文字列
     * @return このオブジェクトへの参照
     */
    public CommandLineParser addOption(String option) {
        return addOption(option, false);
    }

    /**
     * オプションを追加します。
     *
     * @param option   追加するオプション文字列
     * @param required オプションが省略可能か否かを表すブール値 (trueの場合は必須オプション)
     * @return このオブジェクトへの参照
     */
    public CommandLineParser addOption(String option, boolean required) {
        candidates_.put(option, required);
        return this;
    }

    /**
     * 最低限必要な引数の数を設定します。
     *
     * @param requiredArgSize 最低限必要な引数の数
     * @return このオブジェクトへの参照
     */
    public CommandLineParser setRequiredArgSize(int requiredArgSize) {
        requiredArgSize_ = requiredArgSize;
        return this;
    }

    /**
     * コマンドライン引数を解析します。
     *
     * @param args コマンドライン引数
     * @return 引数が不正の場合は false
     */
    public boolean parse(String[] args) {
        // 引数解析
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            String nextArg = (i < args.length - 1) ? args[i + 1] : null;

            if (candidates_.containsKey(arg)) {
                if (candidates_.containsKey(nextArg)) {
                    options_.put(arg, null);
                } else {
                    options_.put(arg, nextArg);
                    i++;
                }

            } else {
                args_.add(arg);
            }
        }

        // 必須オプションがすべて存在するか確認
        for (Entry<String, Boolean> entry : candidates_.entrySet()) {
            if (entry.getValue()) {
                if (!options_.containsKey(entry.getKey())) {
                    return false;
                }
            }
        }

        // オプション以外の引数の個数が足りているか確認
        return requiredArgSize_ <= args_.size();
    }

    /**
     * 指定したオプションが存在するか判定します。
     *
     * @param option オプション文字列
     * @return オプションが存在する場合は true
     */
    public boolean hasOption(String option) {
        return options_.containsKey(option);
    }

    /**
     * 指定したオプションが値を持つか判定します。 オプション自体が存在しない場合は false を返します。
     *
     * @param option オプション文字列
     * @return オプションが値を持つ場合は true
     */
    public boolean hasOptionValue(String option) {
        return options_.get(option) != null;
    }

    /**
     * 指定したオプションの値を取得します。
     *
     * @param option オプション文字列
     * @return オプションの値 (値が存在しない場合は null)
     */
    public String getOptionValue(String option) {
        return getOptionValue(option, null);
    }

    /**
     * デフォルト値を指定して、指定したオプションの値を取得します。
     *
     * @param option   オプション文字列
     * @param defValue 値が存在しない場合のデフォルト値
     * @return オプションの値
     */
    public String getOptionValue(String option, String defValue) {
        String value = options_.get(option);
        return value != null ? value : defValue;
    }

    /**
     * 指定した位置の引数を取得します。
     *
     * @param index 取得する位置
     * @return 引数文字列
     */
    public String getArg(int index) {
        return getArg(index, null);
    }

    /**
     * デフォルト値を指定して、指定した位置の引数を取得します。
     *
     * @param index    取得する位置
     * @param defValue 引数が存在しない場合のデフォルト値
     * @return 引数文字列
     */
    public String getArg(int index, String defValue) {
        if (args_.size() <= index) {
            return defValue;
        }
        return args_.get(index);
    }

    @Override
    public String toString() {
        return "CommandLineParser [options=" + options_ + ", args=" + args_ + "]";
    }
}