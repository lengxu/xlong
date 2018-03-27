package uyun.xianglong.sdk.common.utz;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cmd {

    private static Logger logger = LoggerFactory.getLogger(Cmd.class);

    public static CommandLine build(String[] args, Options options) {
        options.addOption(
                Option.builder("f").hasArg().longOpt("appConfigFile").desc("App Config File Path").build());
        options.addOption(
                Option.builder("s").hasArg().longOpt("appConfigString").desc("App Config String").build());
        options.addOption(
                Option.builder("p").hasArg().longOpt("parameterDefinitionFilePath").desc("App Component parameter definition file path").build()
        );
        CommandLine cmd = null;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (Exception e) {
            logger.error("Parsing failed.  Reason: " + e.getMessage());
            new HelpFormatter().printHelp("[options] ", options);
        }
        return cmd;
    }
}
