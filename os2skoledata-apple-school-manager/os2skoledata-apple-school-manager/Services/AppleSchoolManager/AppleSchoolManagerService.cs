using CsvHelper.Configuration;
using CsvHelper;
using os2skoledata_apple_school_manager.Services.OS2skoledata.Model;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Text;
using System.IO.Compression;
using Renci.SshNet;
using System.Linq;
using Microsoft.Extensions.Logging;

namespace os2skoledata_apple_school_manager.Services.AppleSchoolManager
{
    internal class AppleSchoolManagerService : ServiceBase<AppleSchoolManagerService>
    {
        CsvConfiguration config;
        string domain;
        string sftpUsername;
        string sftpPassword;
        string sftpUrl;
        string zipFileName;
        public AppleSchoolManagerService(IServiceProvider sp) : base(sp)
        {
            config = new CsvConfiguration(CultureInfo.InvariantCulture)
            {
                Encoding = Encoding.UTF8,
                NewLine = Environment.NewLine,
                Quote = '"',
                ShouldQuote = (field) => true
            };

            domain = settings.AppleSchoolManagerSettings.Domain;
            sftpUsername = settings.AppleSchoolManagerSettings.SFTP.Username;
            sftpPassword = settings.AppleSchoolManagerSettings.SFTP.Password;
            sftpUrl = settings.AppleSchoolManagerSettings.SFTP.Url;
            zipFileName = settings.AppleSchoolManagerSettings.ZipFileName;
        }

        private string GenerateStudentsCsv(List<AppleUserDto> students)
        {
            using StringWriter writer = new StringWriter();
            using CsvWriter csv = new CsvWriter(writer, config);

            csv.WriteField("person_id");
            csv.WriteField("person_number");
            csv.WriteField("first_name");
            csv.WriteField("middle_name");
            csv.WriteField("last_name");
            csv.WriteField("grade_level");
            csv.WriteField("email_address");
            csv.WriteField("sis_username");
            csv.WriteField("password_policy");
            csv.WriteField("location_id");
            csv.NextRecord();

            foreach (AppleUserDto user in students)
            {
                csv.WriteField(user.UniId);
                csv.WriteField("");
                csv.WriteField(user.FirstName);
                csv.WriteField("");
                csv.WriteField(user.LastName);
                csv.WriteField(user.Level == null ? "" : user.Level.ToString());
                csv.WriteField(user.Username + "@" + domain);
                csv.WriteField(user.Username);
                csv.WriteField("");
                csv.WriteField("institution_" + user.PrimaryInstitutionId);
                csv.NextRecord();
            }

            logger.LogInformation("Generated students.csv");

            return writer.ToString();
        }

        private string GenerateStaffCsv(List<AppleUserDto> staff)
        {
            using StringWriter writer = new StringWriter();
            using CsvWriter csv = new CsvWriter(writer, config);

            csv.WriteField("person_id");
            csv.WriteField("person_number");
            csv.WriteField("first_name");
            csv.WriteField("middle_name");
            csv.WriteField("last_name");
            csv.WriteField("email_address");
            csv.WriteField("sis_username");
            csv.WriteField("location_id");
            csv.NextRecord();

            foreach (AppleUserDto user in staff)
            {
                csv.WriteField(user.UniId);
                csv.WriteField("");
                csv.WriteField(user.FirstName);
                csv.WriteField("");
                csv.WriteField(user.LastName);
                csv.WriteField(user.Username + "@" + domain);
                csv.WriteField(user.Username);
                csv.WriteField("institution_" + user.PrimaryInstitutionId);
                csv.NextRecord();
            }

            logger.LogInformation("Generated staff.csv");

            return writer.ToString();
        }

        private string GenerateCoursesCsv(List<AppleGroupDto> groups)
        {
            using StringWriter writer = new StringWriter();
            using CsvWriter csv = new CsvWriter(writer, config);

            csv.WriteField("course_id");
            csv.WriteField("course_number");
            csv.WriteField("course_name");
            csv.WriteField("location_id");
            csv.NextRecord();

            foreach (AppleGroupDto course in groups)
            {
                csv.WriteField("course_" + course.Id);
                csv.WriteField(course.StilId);
                csv.WriteField(course.GroupName);
                csv.WriteField("institution_" + course.InstitutionId);
                csv.NextRecord();
            }

            logger.LogInformation("Generated courses.csv");

            return writer.ToString();
        }

        private string GenerateClassesCsv(List<AppleGroupDto> groups)
        {
            using StringWriter writer = new StringWriter();
            using CsvWriter csv = new CsvWriter(writer, config);

            csv.WriteField("class_id");
            csv.WriteField("class_number");
            csv.WriteField("course_id");
            csv.WriteField("instructor_id");
            csv.WriteField("instructor_id_2");
            csv.WriteField("instructor_id_3");
            csv.WriteField("location_id");
            csv.NextRecord();

            foreach (AppleGroupDto group in groups)
            {
                csv.WriteField("class_" + group.Id);
                csv.WriteField(group.StilId);
                csv.WriteField("course_" + group.Id);

                var instructors = group.TeacherUniIds?.Take(3).ToList() ?? new List<string>();

                csv.WriteField(instructors.Count > 0 ? instructors[0] : "");
                csv.WriteField(instructors.Count > 1 ? instructors[1] : "");
                csv.WriteField(instructors.Count > 2 ? instructors[2] : "");

                csv.WriteField("institution_" + group.InstitutionId);
                csv.NextRecord();
            }

            logger.LogInformation("Generated classes.csv");

            return writer.ToString();
        }

        private string GenerateRostersCsv(List<AppleRosterDto> rosters)
        {
            using StringWriter writer = new StringWriter();
            using CsvWriter csv = new CsvWriter(writer, config);

            csv.WriteField("roster_id");
            csv.WriteField("class_id");
            csv.WriteField("student_id");
            csv.NextRecord();

            foreach (AppleRosterDto roster in rosters)
            {
                csv.WriteField(roster.GroupId + "_" + roster.UniId);
                csv.WriteField("class_" + roster.GroupId);
                csv.WriteField(roster.UniId);
                csv.NextRecord();
            }

            logger.LogInformation("Generated rosters.csv");

            return writer.ToString();
        }

        private string GenerateLocationsCsv(List<AppleInstitutionDto> institutions)
        {
            using StringWriter writer = new StringWriter();
            using CsvWriter csv = new CsvWriter(writer, config);

            csv.WriteField("location_id");
            csv.WriteField("location_name");
            csv.NextRecord();

            foreach (AppleInstitutionDto institution in institutions)
            {
                csv.WriteField("institution_" + institution.Id);
                csv.WriteField(institution.Name);
                csv.NextRecord();
            }

            logger.LogInformation("Generated locations.csv");

            return writer.ToString();
        }


        public void ProcessAndUpload(AppleFullLoadDto fullLoad)
        {
            var csvFiles = new Dictionary<string, string>
            {
                ["students.csv"] = GenerateStudentsCsv(fullLoad.Students),
                ["staff.csv"] = GenerateStaffCsv(fullLoad.Staff),
                ["courses.csv"] = GenerateCoursesCsv(fullLoad.Groups),
                ["classes.csv"] = GenerateClassesCsv(fullLoad.Groups),
                ["rosters.csv"] = GenerateRostersCsv(fullLoad.Rosters),
                ["locations.csv"] = GenerateLocationsCsv(fullLoad.Institutions)
            };

            using var zipStream = CreateZipWithCsvFiles(csvFiles);
            logger.LogInformation("Generated zip file");
            string timestamp = DateTime.UtcNow.ToString("yyyyMMdd_HHmmss");
            String fíleName = timestamp + "_" + zipFileName;
            UploadZipToSftp(zipStream, fíleName);
            logger.LogInformation("Uploaded zip file " + fíleName + " via SFTP");
        }

        // ONLY for testing
        public void SaveZipToLocal(AppleFullLoadDto fullLoad, string filePath)
        {
            var csvFiles = new Dictionary<string, string>
            {
                ["students.csv"] = GenerateStudentsCsv(fullLoad.Students),
                ["staff.csv"] = GenerateStaffCsv(fullLoad.Staff),
                ["courses.csv"] = GenerateCoursesCsv(fullLoad.Groups),
                ["classes.csv"] = GenerateClassesCsv(fullLoad.Groups),
                ["rosters.csv"] = GenerateRostersCsv(fullLoad.Rosters),
                ["locations.csv"] = GenerateLocationsCsv(fullLoad.Institutions)
            };

            using var zipStream = CreateZipWithCsvFiles(csvFiles);

            using var fileStream = new FileStream(filePath, FileMode.Create, FileAccess.Write);
            zipStream.CopyTo(fileStream);
        }

        public MemoryStream CreateZipWithCsvFiles(Dictionary<string, string> csvFiles)
        {
            var zipStream = new MemoryStream();

            using (var archive = new ZipArchive(zipStream, ZipArchiveMode.Create, leaveOpen: true))
            {
                foreach (var kvp in csvFiles)
                {
                    var entry = archive.CreateEntry(kvp.Key);
                    using var entryStream = entry.Open();
                    using var writer = new StreamWriter(entryStream, new UTF8Encoding(encoderShouldEmitUTF8Identifier: false));
                    writer.Write(kvp.Value);
                }
            }
            zipStream.Position = 0;
            return zipStream;
        }

        public void UploadZipToSftp(Stream zipStream, string remoteFileName)
        {
            using var sftp = new SftpClient(sftpUrl, sftpUsername, sftpPassword);
            sftp.Connect();
            logger.LogInformation("SFTP: connected");
            sftp.UploadFile(zipStream, remoteFileName, true);
            logger.LogInformation("SFTP: uploaded");
            sftp.Disconnect();
            logger.LogInformation("SFTP: disconnected");
        }

    }
}
