// Copyright 2019 Oath Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.

package com.yahoo.prelude.query;

import com.google.common.annotations.Beta;
import com.yahoo.compress.IntegerCompressor;
import com.yahoo.prelude.query.textualrepresentation.Discloser;

import java.nio.ByteBuffer;

/**
 * Represent a query item matching the K nearest neighbors in a multi-dimensional vector space.
 * The query point vector is referenced by the name of a tensor passed as a query rank feature;
 * specifying "myvector" as the name means the query must set "ranking.features.query(myvector)".
 * This rank feature must be configured with the correct tensor type in the active query profile.
 * The field name (AKA the index name) given must be an attribute, with the exact same tensor type.
 *
 * @author arnej
 */
@Beta
public class NearestNeighborItem extends SimpleTaggableItem {

    private int targetNumHits = 0;
    private int hnswExploreAdditionalHits = 0;
    private boolean approximate = true;
    private String field;
    private final String queryTensorName;

    public NearestNeighborItem(String fieldName, String queryTensorName) {
        this.field = fieldName;
        this.queryTensorName = queryTensorName;
    }

    /** Returns the K number of hits to produce */
    public int getTargetNumHits() { return targetNumHits; }

    /** Returns the field name */
    public String getIndexName() { return field; }

    /** Returns the number of extra hits to explore in HNSW algorithm */
    public int getHnswExploreAdditionalHits() { return hnswExploreAdditionalHits; }

    /** Returns whether approximation is allowed */
    public boolean getAllowApproximate() { return approximate; }

    /** Returns the name of the query tensor */
    public String getQueryTensorName() { return queryTensorName; }

    /** Set the K number of hits to produce */
    public void setTargetNumHits(int target) { this.targetNumHits = target; }

    /** Set the number of extra hits to explore in HNSW algorithm */
    public void setHnswExploreAdditionalHits(int num) { this.hnswExploreAdditionalHits = num; }

    /** Set whether approximation is allowed */
    public void setAllowApproximate(boolean value) { this.approximate = value; }

    @Override
    public void setIndexName(String index) { this.field = index; }

    @Override
    public ItemType getItemType() { return ItemType.NEAREST_NEIGHBOR; }

    @Override
    public String getName() { return "NEAREST_NEIGHBOR"; }

    @Override
    public int getTermCount() { return 1; }

    @Override
    public int encode(ByteBuffer buffer) {
        super.encodeThis(buffer);
        putString(field, buffer);
        putString(queryTensorName, buffer);
        IntegerCompressor.putCompressedPositiveNumber(targetNumHits, buffer);
        IntegerCompressor.putCompressedPositiveNumber((approximate ? 1 : 0), buffer);
        IntegerCompressor.putCompressedPositiveNumber(hnswExploreAdditionalHits, buffer);
        return 1;  // number of encoded stack dump items
    }

    @Override
    protected void appendBodyString(StringBuilder buffer) {
        buffer.append("{field=").append(field);
        buffer.append(",queryTensorName=").append(queryTensorName);
        buffer.append(",hnsw.exploreAdditionalHits=").append(hnswExploreAdditionalHits);
        buffer.append(",approximate=").append(approximate);
        buffer.append(",targetHits=").append(targetNumHits).append("}");
    }

    @Override
    public void disclose(Discloser discloser) {
        super.disclose(discloser);
        discloser.addProperty("field", field);
        discloser.addProperty("queryTensorName", queryTensorName);
        discloser.addProperty("hnsw.exploreAdditionalHits", hnswExploreAdditionalHits);
        discloser.addProperty("approximate", approximate);
        discloser.addProperty("targetHits", targetNumHits);
    }

}
