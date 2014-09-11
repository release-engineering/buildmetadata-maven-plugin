import java.io.File

assert new File(basedir, "build.log").text.contains( "Unable to determine SCM revision information")

return true