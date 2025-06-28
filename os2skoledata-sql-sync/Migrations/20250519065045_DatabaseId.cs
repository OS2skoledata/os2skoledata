using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace os2skoledata_sql_sync.Migrations
{
    public partial class DatabaseId : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "os2skoledata_database_id",
                table: "institution_persons",
                type: "int",
                nullable: false,
                defaultValue: 0);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "os2skoledata_database_id",
                table: "institution_persons");
        }
    }
}
