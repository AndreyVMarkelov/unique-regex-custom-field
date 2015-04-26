package ru.andreymarkelov.atlas.plugins.ur.manager;

import ru.andreymarkelov.atlas.plugins.ur.model.CFData;

public interface UniqueRegexMgr {
    CFData getCFData(String cfKey);
    void setCfJql(String cfKey, String jql);
    void setCfRegex(String cfKey, String regex);
    void setCfRegexError(String cfKey, String regexError);
    void setCfTarget(String cfKey, String target);
}
