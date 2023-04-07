using Newtonsoft.Json.Converters;
using System.Runtime.Serialization;

namespace os2skoledata_sql_sync.Services.OS2skoledata
{
    public class CustomDateTimeConverter : IsoDateTimeConverter
    {
        public CustomDateTimeConverter()
        {
            DateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        }

        public CustomDateTimeConverter(string format)
        {
            DateTimeFormat = format;
        }
    }
}