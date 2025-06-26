package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        String sql = "SELECT category_id, name, description FROM categories";
        List<Category> categories = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery())
        {
            while (rs.next())
            {
                categories.add(mapRow(rs));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error fetching categories", e);
        }

        return categories;
    }

    public Category getById(int categoryId)
    {
        String sql = "SELECT category_id, name, description FROM categories WHERE category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    return mapRow(rs);
                }
                return null;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error fetching category by ID", e);
        }
    }

    //aded an implemtaion that doesnt allow duplication in catgories
    @Override
    public Category create(Category category)
    {
        // Check if a category with the same name already exists
        String checkSql = "SELECT category_id, name, description FROM categories WHERE name = ?";

        try (Connection conn = getConnection())
        {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql))
            {
                checkStmt.setString(1, category.getName());

                try (ResultSet rs = checkStmt.executeQuery())
                {
                    if (rs.next())
                    {
                        Category existing = new Category();
                        existing.setCategoryId(rs.getInt("category_id"));
                        existing.setName(rs.getString("name"));
                        existing.setDescription(rs.getString("description"));

                        return existing;
                    }
                }
            }

            // If it doesn't exist, insert it
            String insertSql = "INSERT INTO categories (name, description) VALUES (?, ?)";

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS))
            {
                insertStmt.setString(1, category.getName());
                insertStmt.setString(2, category.getDescription());

                insertStmt.executeUpdate();

                try (ResultSet keys = insertStmt.getGeneratedKeys())
                {
                    if (keys.next())
                    {
                        category.setCategoryId(keys.getInt(1));
                    }
                }

                return category;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error creating or finding category", e);
        }
    }



    @Override
    public void update(int categoryId, Category category)
    {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, categoryId);

            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error updating category", e);
        }
    }

    @Override
    public void delete(int categoryId)
    {
        String sql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, categoryId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0)
            {
                // No category found with this id
                throw new RuntimeException("Category with id " + categoryId + " does not exist.");
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error deleting category", e);
        }
    }


    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
