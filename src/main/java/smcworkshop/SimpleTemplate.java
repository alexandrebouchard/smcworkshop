package smcworkshop;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.mvel2.templates.TemplateRuntime;

import com.google.common.collect.FluentIterable;

import binc.Command;
import briefj.BriefIO;
import briefj.opt.Option;
import briefj.run.Mains;
import briefj.run.Results;

public class SimpleTemplate implements Runnable
{
  @Option public String templateLocation = "files/template.tex";
  @Option public String outFile = "output.tex";
  @Option public int nLatexRuns = 2;
  @Option public String dataPath = "files/program_data.csv";
  
  public String files;
  public List<File> abstracts_posters;

  @Override
  public void run()
  {
    files = new File("files").getAbsolutePath() + "/";
    abstracts_posters = briefj.BriefFiles.ls(new java.io.File("files/abstracts_posters"), "tex");
    String template = BriefIO.fileToString(new File(templateLocation));
    String result = (String) TemplateRuntime.eval(template, this);
    File texFile = Results.getFileInResultFolder(outFile);
    BriefIO.write(texFile, result);
    String pdflatexOut = "LATEX NOT RUN, SET nLatexRuns TO MORE THAN ZERO";
    for (int i = 0; i < nLatexRuns; i++)
      pdflatexOut = Command.byName("pdflatex").withArgs(texFile.getName()).ranIn(texFile.getParentFile()).callWithInputStreamContents("x\nx\nx\nx\nx\nx\n");
    System.out.println("Latex output (last run)\n" + pdflatexOut);
    Command.call(Command.byPath(new File("/usr/bin/open")).withArgs(Results.getFileInResultFolder(outFile.replaceAll("tex$", "pdf")).getAbsolutePath()));
  }
  
  public String authorKey(Map<String, String> line)
  {
    String fullName = line.get("speaker");
    String [] names = fullName.split("\\s+"); 
    return names[names.length - 1];
  }
  
  public FluentIterable<Map<String,String>> parseCSV() 
  {
    return BriefIO.readLines(dataPath).indexCSV();
  }
  
  public static void main(String[] args)
  {
    Mains.instrumentedRun(args, new SimpleTemplate());
  }
}
