using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using os2skoledata_sql_sync.Model;

namespace os2skoledata_sql_sync.Config
{
    public class EFContext : DbContext
    {
        private readonly string connectionString;
        public EFContext(IConfiguration configuration)
        {
            connectionString = configuration.GetValue<string>("DatabaseSettings:ConnectionString");
            Database.Migrate();
        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseMySql(connectionString, ServerVersion.AutoDetect(connectionString));
            optionsBuilder.UseSnakeCaseNamingConvention();
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Institution>()
                .HasMany(i => i.InstitutionPersons)
                .WithOne(ip => ip.Institution)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Institution>()
                .HasMany(i => i.Groups)
                .WithOne(g => g.Institution)
                .OnDelete(DeleteBehavior.Cascade);

            // Eager Loading of Related Data
            modelBuilder.Entity<Institution>().Navigation(i => i.Groups).AutoInclude();

            modelBuilder.Entity<Group>()
                .Property(g => g.GroupType).HasConversion<string>();
            modelBuilder.Entity<Group>()
                  .Property(g => g.FromDate)
                  .HasColumnType("date");
            modelBuilder.Entity<Group>()
                  .Property(g => g.ToDate)
                  .HasColumnType("date");

            modelBuilder.Entity<InstitutionPerson>()
                .HasOne(ip => ip.Employee);
            modelBuilder.Entity<InstitutionPerson>()
                .HasOne(ip => ip.Extern);
            modelBuilder.Entity<InstitutionPerson>()
                .HasOne(ip => ip.Student);
            modelBuilder.Entity<InstitutionPerson>()
                .HasOne(ip => ip.Person);
            modelBuilder.Entity<InstitutionPerson>()
                .HasOne(ip => ip.UniLogin);

            modelBuilder.Entity<Employee>()
                .HasMany(e => e.Roles)
                .WithOne(r => r.Employee)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Employee>()
                .HasMany(e => e.GroupIds)
                .WithOne(x => x.Employee)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Role>()
                .Property(r => r.EmployeeRole).HasConversion<string>();

            modelBuilder.Entity<Extern>()
                .Property(r => r.Role).HasConversion<string>();

            modelBuilder.Entity<Extern>()
                .HasMany(e => e.GroupIds)
                .WithOne(x => x.Extern)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Student>()
                .Property(r => r.Role).HasConversion<string>();

            modelBuilder.Entity<Student>()
                .HasMany(e => e.ContactPersons)
                .WithOne(x => x.Student)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Student>()
                .HasMany(e => e.GroupIds)
                .WithOne(x => x.Student)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<ContactPerson>()
                .HasOne(c => c.UniLogin);
            modelBuilder.Entity<ContactPerson>()
                .HasOne(c => c.Person);
            modelBuilder.Entity<ContactPerson>()
             .Property(p => p.Relation).HasConversion<string>();

            modelBuilder.Entity<Person>()
                .HasOne(p => p.Address);
            modelBuilder.Entity<Person>()
                .HasOne(p => p.HomePhoneNumber);
            modelBuilder.Entity<Person>()
                .HasOne(p => p.MobilePhoneNumber);
            modelBuilder.Entity<Person>()
                .HasOne(p => p.WorkPhoneNumber);

            modelBuilder.Entity<Person>()
                .Property(p => p.Gender).HasConversion<string>();

            modelBuilder.Entity<Person>()
                  .Property(p => p.BirthDate)
                  .HasColumnType("date");

            modelBuilder.Entity<Address>()
                .Property(p => p.CountryCode).HasConversion<string>();

            modelBuilder.Entity<UniLogin>()
                .Property(p => p.PasswordState).HasConversion<string>();

            modelBuilder.Entity<Institution>()
                .Property(i => i.type).HasConversion<string>();

        }

        public DbSet<Institution> Institutions { get; set; }
        public DbSet<InstitutionPerson> InstitutionPersons { get; set; }
        public DbSet<Employee> Employees { get; set; }

        public DbSet<Extern> Externs { get; set; }
        public DbSet<Student> Students { get; set; }
        public DbSet<ContactPerson> ContactPersons { get; set; }
        public DbSet<Person> Persons { get; set; }
        public DbSet<Address> Addresses { get; set; }
        public DbSet<PhoneNumber> Phonenumbers { get; set; }
        public DbSet<UniLogin> UniLogins { get; set; }
        public DbSet<Group> Groups { get; set; }
    }
}
