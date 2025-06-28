using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace os2skoledata_sql_sync.Migrations
{
    public partial class reservedUsernameAndPrimary : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<bool>(
                name: "primary_institution",
                table: "institution_persons",
                type: "tinyint(1)",
                nullable: false,
                defaultValue: false);

            migrationBuilder.AddColumn<string>(
                name: "reserved_username",
                table: "institution_persons",
                type: "longtext",
                nullable: true)
                .Annotation("MySql:CharSet", "utf8mb4");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "primary_institution",
                table: "institution_persons");

            migrationBuilder.DropColumn(
                name: "reserved_username",
                table: "institution_persons");
        }
    }
}
