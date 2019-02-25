package io.graversen.rust.rcon.objects.rust;

import com.google.gson.annotations.SerializedName;

public class BuildInfo
{
    @SerializedName("Date")
    private long buildCreatedAt;

    @SerializedName("Build")
    private Build build;

    public long getBuildCreatedAt()
    {
        return buildCreatedAt;
    }

    public Build getBuild()
    {
        return build;
    }

    private class Build
    {
        @SerializedName("Id")
        private String id;

        @SerializedName("Number")
        private String number;

        @SerializedName("Tag")
        private String jenkinsTag;

        @SerializedName("Url")
        private String jenkinsUrl;

        public String getId()
        {
            return id;
        }

        public String getNumber()
        {
            return number;
        }

        public String getJenkinsTag()
        {
            return jenkinsTag;
        }

        public String getJenkinsUrl()
        {
            return jenkinsUrl;
        }
    }
}
