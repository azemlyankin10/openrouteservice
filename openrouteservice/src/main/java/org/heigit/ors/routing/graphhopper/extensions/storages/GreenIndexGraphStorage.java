/*  This file is part of Openrouteservice.
 *
 *  Openrouteservice is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 
 *  of the License, or (at your option) any later version.

 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License along with this library; 
 *  if not, see <https://www.gnu.org/licenses/>.  
 */
package org.heigit.ors.routing.graphhopper.extensions.storages;

import com.graphhopper.storage.*;

public class GreenIndexGraphStorage implements GraphExtension {
    /* pointer for no entry */
    protected static final int NO_ENTRY = -1;
    private static final int EF_GREENINDEX = 0;

    private DataAccess orsEdges;
    private int edgeEntryBytes;
    private int edgesCount; // number of edges with custom values

    private final byte[] byteValues;

    public GreenIndexGraphStorage() {
        int edgeEntryIndex = 0;
        edgeEntryBytes = edgeEntryIndex + 1;
        edgesCount = 0;
        byteValues = new byte[1];
    }

    public void setEdgeValue(int edgeId, byte greenIndex) {
        edgesCount++;
        ensureEdgesIndex(edgeId);

        // add entry
        long edgePointer = (long) edgeId * edgeEntryBytes;
        byteValues[0] = greenIndex;
        orsEdges.setBytes(edgePointer + EF_GREENINDEX, byteValues, 1);
    }

    private void ensureEdgesIndex(int edgeId) {
        orsEdges.ensureCapacity(((long) edgeId + 1) * edgeEntryBytes);
    }

    public int getEdgeValue(int edgeId, byte[] buffer) {
        // TODO this needs further checking when implementing the Weighting classes/functions
        long edgePointer = (long) edgeId * edgeEntryBytes;
        orsEdges.getBytes(edgePointer + EF_GREENINDEX, buffer, 1);

        return buffer[0];
    }

    /**
     * @return true if successfully loaded from persistent storage.
     */
    // TODO how to deal with @Override
    public boolean loadExisting() {
        if (!orsEdges.loadExisting())
            throw new IllegalStateException("Unable to load storage 'ext_greenindex'. corrupt file or directory?");

        edgeEntryBytes = orsEdges.getHeader(0);
        edgesCount = orsEdges.getHeader(4);
        return true;
    }

    /**
     * Creates the underlying storage. First operation if it cannot be loaded.
     *
     * @param initBytes
     */
    // TODO how to deal with @Override
    public GreenIndexGraphStorage create(long initBytes) {
        orsEdges.create(initBytes * edgeEntryBytes);
        return this;
    }

    /**
     * This method makes sure that the underlying data is written to the storage. Keep in mind that
     * a disc normally has an IO cache so that flush() is (less) probably not save against power
     * loses.
     */
    // TODO how to deal with @Override
    public void flush() {
        orsEdges.setHeader(0, edgeEntryBytes);
        orsEdges.setHeader(4, edgesCount);
        orsEdges.flush();
    }

    /**
     * This method makes sure that the underlying used resources are released. WARNING: it does NOT
     * flush on close!
     */
    @Override
    public void close() { orsEdges.close(); }

    @Override
    public boolean isClosed() {
        return false;
    }

    /**
     * @return the allocated storage size in bytes
     */
    // TODO how to deal with @Override
    public long getCapacity() {
        return orsEdges.getCapacity();
    }
}
