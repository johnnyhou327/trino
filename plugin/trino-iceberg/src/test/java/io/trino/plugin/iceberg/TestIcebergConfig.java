/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.plugin.iceberg;

import com.google.common.collect.ImmutableMap;
import io.airlift.units.Duration;
import io.trino.plugin.hive.HiveCompressionCodec;
import org.testng.annotations.Test;

import java.util.Map;

import static io.airlift.configuration.testing.ConfigAssertions.assertFullMapping;
import static io.airlift.configuration.testing.ConfigAssertions.assertRecordedDefaults;
import static io.airlift.configuration.testing.ConfigAssertions.recordDefaults;
import static io.trino.plugin.hive.HiveCompressionCodec.ZSTD;
import static io.trino.plugin.iceberg.CatalogType.GLUE;
import static io.trino.plugin.iceberg.CatalogType.HIVE_METASTORE;
import static io.trino.plugin.iceberg.IcebergFileFormat.ORC;
import static io.trino.plugin.iceberg.IcebergFileFormat.PARQUET;
import static java.util.concurrent.TimeUnit.MINUTES;

public class TestIcebergConfig
{
    @Test
    public void testDefaults()
    {
        assertRecordedDefaults(recordDefaults(IcebergConfig.class)
                .setFileFormat(ORC)
                .setCompressionCodec(ZSTD)
                .setUseFileSizeFromMetadata(true)
                .setMaxPartitionsPerWriter(100)
                .setUniqueTableLocation(false)
                .setCatalogType(HIVE_METASTORE)
                .setDynamicFilteringWaitTimeout(new Duration(0, MINUTES))
                .setTableStatisticsEnabled(true)
                .setProjectionPushdownEnabled(true)
                .setHiveCatalogName(null)
                .setFormatVersion(1));
    }

    @Test
    public void testExplicitPropertyMappings()
    {
        Map<String, String> properties = ImmutableMap.<String, String>builder()
                .put("iceberg.file-format", "Parquet")
                .put("iceberg.compression-codec", "NONE")
                .put("iceberg.use-file-size-from-metadata", "false")
                .put("iceberg.max-partitions-per-writer", "222")
                .put("iceberg.unique-table-location", "true")
                .put("iceberg.catalog.type", "GLUE")
                .put("iceberg.dynamic-filtering.wait-timeout", "1h")
                .put("iceberg.table-statistics-enabled", "false")
                .put("iceberg.projection-pushdown-enabled", "false")
                .put("iceberg.hive-catalog-name", "hive")
                .put("iceberg.format-version", "2")
                .buildOrThrow();

        IcebergConfig expected = new IcebergConfig()
                .setFileFormat(PARQUET)
                .setCompressionCodec(HiveCompressionCodec.NONE)
                .setUseFileSizeFromMetadata(false)
                .setMaxPartitionsPerWriter(222)
                .setUniqueTableLocation(true)
                .setCatalogType(GLUE)
                .setDynamicFilteringWaitTimeout(Duration.valueOf("1h"))
                .setTableStatisticsEnabled(false)
                .setProjectionPushdownEnabled(false)
                .setHiveCatalogName("hive")
                .setFormatVersion(2);

        assertFullMapping(properties, expected);
    }
}
