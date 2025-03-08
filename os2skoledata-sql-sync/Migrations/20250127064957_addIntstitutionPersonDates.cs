using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace os2skoledata_sql_sync.Migrations
{
    public partial class addIntstitutionPersonDates : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<DateTime>(
                name: "ad_created",
                table: "institution_persons",
                type: "datetime(6)",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));

            migrationBuilder.AddColumn<DateTime>(
                name: "ad_deactivated",
                table: "institution_persons",
                type: "datetime(6)",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));

            migrationBuilder.AddColumn<DateTime>(
                name: "azure_created",
                table: "institution_persons",
                type: "datetime(6)",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));

            migrationBuilder.AddColumn<DateTime>(
                name: "azure_deactivated",
                table: "institution_persons",
                type: "datetime(6)",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));

            migrationBuilder.AddColumn<DateTime>(
                name: "gw_created",
                table: "institution_persons",
                type: "datetime(6)",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));

            migrationBuilder.AddColumn<DateTime>(
                name: "gw_deactivated",
                table: "institution_persons",
                type: "datetime(6)",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));

            migrationBuilder.AddColumn<DateTime>(
                name: "stil_created",
                table: "institution_persons",
                type: "datetime(6)",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));

            migrationBuilder.AddColumn<DateTime>(
                name: "stil_deleted",
                table: "institution_persons",
                type: "datetime(6)",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "ad_created",
                table: "institution_persons");

            migrationBuilder.DropColumn(
                name: "ad_deactivated",
                table: "institution_persons");

            migrationBuilder.DropColumn(
                name: "azure_created",
                table: "institution_persons");

            migrationBuilder.DropColumn(
                name: "azure_deactivated",
                table: "institution_persons");

            migrationBuilder.DropColumn(
                name: "gw_created",
                table: "institution_persons");

            migrationBuilder.DropColumn(
                name: "gw_deactivated",
                table: "institution_persons");

            migrationBuilder.DropColumn(
                name: "stil_created",
                table: "institution_persons");

            migrationBuilder.DropColumn(
                name: "stil_deleted",
                table: "institution_persons");
        }
    }
}
